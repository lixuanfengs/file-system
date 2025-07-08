// 文件管理页面 JavaScript

// 文件历史记录
let fileHistory = JSON.parse(localStorage.getItem('fileHistory') || '[]');

// 页面加载时的初始化
document.addEventListener('DOMContentLoaded', function() {
    loadFileHistory();
});

// 清除文件Key
function clearFileKey() {
    document.getElementById('fileKeyInput').value = '';
}

// 获取文件信息
async function getFileInfo() {
    const fileKey = document.getElementById('fileKeyInput').value.trim();
    const resultDiv = document.getElementById('operationResult');

    if (!fileKey) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入文件Key</span>
            </div>
        `;
        return;
    }

    try {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700 animate-pulse';
        resultDiv.innerHTML = '🔄 正在获取文件信息...';

        const response = await fetch(`${FileServer.baseUrl}/file/getInfo?fileKey=${encodeURIComponent(fileKey)}`);
        const data = await response.json();

        if (data.success) {
            const info = data.result;
            resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
            resultDiv.innerHTML = `
                <div class="space-y-4">
                    <div class="flex items-center">
                        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                        <span class="font-semibold">✅ 文件信息获取成功</span>
                    </div>
                    <div class="bg-white p-4 rounded-lg border space-y-3">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                            <div><span class="font-semibold text-gray-700">UUID:</span> <span class="font-mono text-gray-600">${info.uuid}</span></div>
                            <div><span class="font-semibold text-gray-700">文件名:</span> <span class="text-gray-600">${info.fileName}</span></div>
                            <div><span class="font-semibold text-gray-700">大小:</span> <span class="text-gray-600">${FileServerUtils.formatFileSize(info.size)}</span></div>
                            <div><span class="font-semibold text-gray-700">类型:</span> <span class="text-gray-600">${info.contentType}</span></div>
                            <div><span class="font-semibold text-gray-700">后缀:</span> <span class="text-gray-600">${info.subfix}</span></div>
                            <div><span class="font-semibold text-gray-700">创建时间:</span> <span class="text-gray-600">${info.createTime}</span></div>
                        </div>
                    </div>
                </div>
            `;
            addToHistory(fileKey, info.fileName, '获取信息');
        } else {
            resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <div>
                        <div class="font-semibold">❌ 获取失败</div>
                        <div class="text-sm mt-1">错误信息: ${data.message}</div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <div>
                    <div class="font-semibold">❌ 获取失败</div>
                    <div class="text-sm mt-1">网络错误: ${error.message}</div>
                </div>
            </div>
        `;
    }
}

// 下载文件
function downloadFile() {
    const fileKey = document.getElementById('fileKeyInput').value.trim();
    const resultDiv = document.getElementById('operationResult');

    if (!fileKey) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入文件Key</span>
            </div>
        `;
        return;
    }

    resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span>📥 正在准备下载...</span>
        </div>
    `;

    try {
        window.open(`${FileServer.baseUrl}/file/${encodeURIComponent(fileKey)}`, '_blank');
        addToHistory(fileKey, 'Unknown', '下载');
        
        setTimeout(() => {
            resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <span class="font-semibold">✅ 下载已开始</span>
                </div>
            `;
        }, 1000);
    } catch (error) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <div>
                    <div class="font-semibold">❌ 下载失败</div>
                    <div class="text-sm mt-1">错误信息: ${error.message}</div>
                </div>
            </div>
        `;
    }
}

// 预览文件
function previewFile() {
    const fileKey = document.getElementById('fileKeyInput').value.trim();
    const resultDiv = document.getElementById('operationResult');

    if (!fileKey) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入文件Key</span>
            </div>
        `;
        return;
    }

    resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
            </svg>
            <span>👁️ 正在打开预览...</span>
        </div>
    `;

    try {
        window.open(`${FileServer.baseUrl}/file/viewFile/${encodeURIComponent(fileKey)}`, '_blank');
        addToHistory(fileKey, 'Unknown', '预览');
        
        setTimeout(() => {
            resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <span class="font-semibold">✅ 预览已打开</span>
                </div>
            `;
        }, 1000);
    } catch (error) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <div>
                    <div class="font-semibold">❌ 预览失败</div>
                    <div class="text-sm mt-1">错误信息: ${error.message}</div>
                </div>
            </div>
        `;
    }
}

// 删除文件
async function deleteFile() {
    const fileKey = document.getElementById('fileKeyInput').value.trim();
    const resultDiv = document.getElementById('operationResult');

    if (!fileKey) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入文件Key</span>
            </div>
        `;
        return;
    }

    if (!confirm('⚠️ 确定要删除这个文件吗？\n\n此操作不可撤销！')) {
        return;
    }

    try {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-yellow-100 border border-yellow-400 text-yellow-700 animate-pulse';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                </svg>
                <span>🗑️ 正在删除文件...</span>
            </div>
        `;

        const formData = new FormData();
        formData.append('fileKey', fileKey);

        const response = await fetch(`${FileServer.baseUrl}/file/delete`, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();

        if (data.success) {
            resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <div>
                        <div class="font-semibold">✅ 删除成功!</div>
                        <div class="text-sm mt-1">文件已从服务器中移除</div>
                    </div>
                </div>
            `;
            addToHistory(fileKey, 'Unknown', '删除');

            // 清空输入框
            document.getElementById('fileKeyInput').value = '';
        } else {
            resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <div>
                        <div class="font-semibold">❌ 删除失败</div>
                        <div class="text-sm mt-1">错误信息: ${data.message}</div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        resultDiv.className = 'mt-8 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <div>
                    <div class="font-semibold">❌ 删除失败</div>
                    <div class="text-sm mt-1">网络错误: ${error.message}</div>
                </div>
            </div>
        `;
    }
}

// 批量下载
function batchDownload() {
    const batchKeys = document.getElementById('batchFileKeys').value.trim();
    const resultDiv = document.getElementById('batchResult');

    if (!batchKeys) {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入文件Key</span>
            </div>
        `;
        return;
    }

    const fileKeys = batchKeys.split('\n').map(key => key.trim()).filter(key => key);

    if (fileKeys.length === 0) {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入有效的文件Key</span>
            </div>
        `;
        return;
    }

    resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span>📥 正在批量下载 ${fileKeys.length} 个文件...</span>
        </div>
    `;

    // 批量下载文件
    fileKeys.forEach((fileKey, index) => {
        setTimeout(() => {
            try {
                window.open(`${FileServer.baseUrl}/file/${encodeURIComponent(fileKey)}`, '_blank');
                addToHistory(fileKey, 'Unknown', '下载');
            } catch (error) {
                console.error(`下载文件 ${fileKey} 失败:`, error);
            }
        }, index * 500); // 每个文件间隔500ms下载
    });

    setTimeout(() => {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">✅ 批量下载已开始 (${fileKeys.length} 个文件)</span>
            </div>
        `;
    }, 1000);
}

// 批量删除
async function batchDelete() {
    const batchKeys = document.getElementById('batchFileKeys').value.trim();
    const resultDiv = document.getElementById('batchResult');

    if (!batchKeys) {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入文件Key</span>
            </div>
        `;
        return;
    }

    const fileKeys = batchKeys.split('\n').map(key => key.trim()).filter(key => key);

    if (fileKeys.length === 0) {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请输入有效的文件Key</span>
            </div>
        `;
        return;
    }

    if (!confirm(`⚠️ 确定要删除 ${fileKeys.length} 个文件吗？\n\n此操作不可撤销！`)) {
        return;
    }

    resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-yellow-100 border border-yellow-400 text-yellow-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span>🗑️ 正在批量删除 ${fileKeys.length} 个文件...</span>
        </div>
    `;

    let successCount = 0;
    let failCount = 0;

    for (const fileKey of fileKeys) {
        try {
            const formData = new FormData();
            formData.append('fileKey', fileKey);

            const response = await fetch(`${FileServer.baseUrl}/file/delete`, {
                method: 'POST',
                body: formData
            });
            const data = await response.json();

            if (data.success) {
                successCount++;
                addToHistory(fileKey, 'Unknown', '删除');
            } else {
                failCount++;
            }
        } catch (error) {
            failCount++;
        }
    }

    if (successCount > 0) {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">✅ 批量删除完成: 成功 ${successCount} 个${failCount > 0 ? `，失败 ${failCount} 个` : ''}</span>
            </div>
        `;

        // 清空输入框
        document.getElementById('batchFileKeys').value = '';
    } else {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 批量删除失败</span>
            </div>
        `;
    }
}

// 添加到历史记录
function addToHistory(fileKey, fileName, operation) {
    const historyItem = {
        fileKey: fileKey,
        fileName: fileName || 'Unknown',
        operation: operation,
        timestamp: new Date().toLocaleString('zh-CN')
    };

    fileHistory.unshift(historyItem);
    if (fileHistory.length > 20) {
        fileHistory = fileHistory.slice(0, 20);
    }

    localStorage.setItem('fileHistory', JSON.stringify(fileHistory));
    loadFileHistory();
}

// 加载历史记录
function loadFileHistory() {
    const historyDiv = document.getElementById('fileHistory');
    if (fileHistory.length === 0) {
        historyDiv.innerHTML = `
            <div class="text-center py-12">
                <svg class="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-3-3v6m-3-9.75h.008v.008H12V2.25z"></path>
                </svg>
                <p class="text-gray-500 text-lg">暂无操作记录</p>
                <p class="text-gray-400 text-sm mt-2">操作文件后，历史记录将显示在这里</p>
            </div>
        `;
        return;
    }

    let html = '';
    fileHistory.forEach((item, index) => {
        const operationColor = {
            '上传': 'text-green-600 bg-green-100',
            '下载': 'text-blue-600 bg-blue-100',
            '预览': 'text-yellow-600 bg-yellow-100',
            '删除': 'text-red-600 bg-red-100',
            '获取信息': 'text-purple-600 bg-purple-100'
        };

        html += `
            <div class="flex items-center justify-between p-3 bg-white rounded-lg hover:bg-gray-50 transition-colors duration-200 border border-gray-100">
                <div class="flex-1">
                    <div class="flex items-center space-x-2">
                        <span class="px-2 py-1 text-xs rounded-full ${operationColor[item.operation] || 'text-gray-600 bg-gray-100'}">${item.operation}</span>
                        <span class="font-medium text-sm text-gray-900">${item.fileName}</span>
                    </div>
                    <div class="flex items-center space-x-2 mt-1">
                        <span class="text-xs text-gray-500">${item.timestamp}</span>
                        <span class="text-xs text-gray-400 font-mono">${item.fileKey.substring(0, 8)}...</span>
                    </div>
                </div>
                <button onclick="useFileKey('${item.fileKey}')" class="text-blue-600 hover:text-blue-800 text-sm px-3 py-1 rounded-lg hover:bg-blue-50 transition-colors duration-200">
                    使用
                </button>
            </div>
        `;
    });

    historyDiv.innerHTML = html;
}

// 使用历史记录中的文件Key
function useFileKey(fileKey) {
    document.getElementById('fileKeyInput').value = fileKey;
    FileServerUtils.showToast('文件Key已填入', 'success');
}

// 清空历史记录
function clearHistory() {
    if (confirm('确定要清空所有历史记录吗？')) {
        fileHistory = [];
        localStorage.removeItem('fileHistory');
        loadFileHistory();
        FileServerUtils.showToast('历史记录已清空', 'success');
    }
}
