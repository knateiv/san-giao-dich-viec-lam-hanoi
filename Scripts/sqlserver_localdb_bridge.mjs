import net from 'node:net';
import { execSync } from 'node:child_process';

const LISTEN_HOST = '127.0.0.1';
const LISTEN_PORT = Number(process.env.SQL_BRIDGE_PORT || 14333);
const INSTANCE_NAME = process.env.LOCALDB_INSTANCE || 'MSSQLLocalDB';

function getLocalDbPipePath(forceRefresh = false) {
  let out = execSync(`sqllocaldb info "${INSTANCE_NAME}"`, { encoding: 'utf8' });
  if (forceRefresh || !/State:\s*Running/i.test(out)) {
    execSync(`sqllocaldb start "${INSTANCE_NAME}"`, { encoding: 'utf8' });
    out = execSync(`sqllocaldb info "${INSTANCE_NAME}"`, { encoding: 'utf8' });
  }
  const match = out.match(/Instance pipe name:\s*(?:np:)?(\\\\\.\\pipe\\[^\r\n]+)/i);
  if (!match) {
    throw new Error(`Không tìm thấy Named Pipe của LocalDB (${INSTANCE_NAME}):\n${out}`);
  }
  return match[1].trim();
}

let cachedPipePath = getLocalDbPipePath();
console.log(`[SQL Bridge] LocalDB "${INSTANCE_NAME}" pipe: ${cachedPipePath}`);

function connectPipeForClient(clientSocket, allowRetry = true) {
  let connected = false;
  const pipeSocket = net.connect({ path: cachedPipePath });

  pipeSocket.once('connect', () => {
    connected = true;
    clientSocket.pipe(pipeSocket);
    pipeSocket.pipe(clientSocket);
  });

  pipeSocket.on('error', (err) => {
    if (!connected && allowRetry && (err.code === 'ENOENT' || err.code === 'ECONNREFUSED')) {
      try {
        cachedPipePath = getLocalDbPipePath(true);
        console.log(`[SQL Bridge] Auto-refreshed pipe path: ${cachedPipePath}`);
        connectPipeForClient(clientSocket, false);
        return;
      } catch (refreshErr) {
        console.error('[SQL Bridge] Failed refreshing LocalDB:', refreshErr.message);
      }
    }
    if (!clientSocket.destroyed) clientSocket.destroy();
  });

  clientSocket.on('error', () => {
    if (!pipeSocket.destroyed) pipeSocket.destroy();
  });
  clientSocket.on('close', () => {
    if (!pipeSocket.destroyed) pipeSocket.destroy();
  });
  pipeSocket.on('close', () => {
    if (!clientSocket.destroyed) clientSocket.destroy();
  });
}

const server = net.createServer((clientSocket) => {
  clientSocket.setNoDelay(true);
  connectPipeForClient(clientSocket, true);
});

server.on('error', (err) => {
  console.error('[SQL Bridge] Server error:', err.message);
  process.exit(1);
});

server.listen(LISTEN_PORT, LISTEN_HOST, () => {
  console.log(`[SQL Bridge] Đang lắng nghe TCP ${LISTEN_HOST}:${LISTEN_PORT} -> ${cachedPipePath}`);
});
