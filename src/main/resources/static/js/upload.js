// 文件上传页面 JavaScript

// 拖拽处理函数
function handleDragOver(event) {
    event.preventDefault();
    event.currentTarget.classList.add('drag-over');
}

function handleDragLeave(event) {
    event.preventDefault();
    event.currentTarget.classList.remove('drag-over');
}

function handleDrop(event, type) {
    event.preventDefault();
    event.currentTarget.classList.remove('drag-over');
    
    const files = event.dataTransfer.files;
    if (files.length > 0) {
        if (type === 'single') {
            document.getElementById('fileInput').files = files;
            handleFileSelect(document.getElementById('fileInput'), 'single');
        } else {
            document.getElementById('multiFileInput').files = files;
            handleFileSelect(document.getElementById('multiFileInput'), 'multi');
        }
    }
}

// 文件选择处理
function handleFileSelect(input, type) {
    const files = input.files;
    if (files.length === 0) return;

    if (type === 'single') {
        const file = files[0];
        const infoDiv = document.getElementById('singleFileInfo');
        infoDiv.innerHTML = `
            <div class="flex items-center space-x-3">
                <div class="flex-shrink-0">
                    <svg class="w-8 h-8 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-3-3v6m5 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                    </svg>
                </div>
                <div class="flex-1">
                    <p class="font-medium text-gray-900">${file.name}</p>
                    <p class="text-sm text-gray-500">${FileServerUtils.formatFileSize(file.size)}</p>
                </div>
            </div>
        `;
        infoDiv.classList.remove('hidden');
    } else {
        const infoDiv = document.getElementById('multiFileInfo');
        let html = '<div class="space-y-2">';
        for (let i = 0; i < files.length; i++) {
            const file = files[i];
            html += `
                <div class="flex items-center space-x-3 p-2 bg-white rounded border">
                    <svg class="w-6 h-6 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-3-3v6m5 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                    </svg>
                    <div class="flex-1">
                        <p class="font-medium text-gray-900 text-sm">${file.name}</p>
                        <p class="text-xs text-gray-500">${FileServerUtils.formatFileSize(file.size)}</p>
                    </div>
                </div>
            `;
        }
        html += '</div>';
        infoDiv.innerHTML = html;
        infoDiv.classList.remove('hidden');
    }
}

// 清除文件选择
function clearSingleFile() {
    document.getElementById('fileInput').value = '';
    document.getElementById('singleFileInfo').classList.add('hidden');
    document.getElementById('singleProgress').classList.add('hidden');
}

function clearMultiFiles() {
    document.getElementById('multiFileInput').value = '';
    document.getElementById('multiFileInfo').classList.add('hidden');
    document.getElementById('multiProgress').classList.add('hidden');
}

// 显示进度条
function showProgress(type, show = true) {
    const progressDiv = document.getElementById(type + 'Progress');
    if (show) {
        progressDiv.classList.remove('hidden');
        updateProgress(type, 0);
    } else {
        progressDiv.classList.add('hidden');
    }
}

function updateProgress(type, percent) {
    const progressBar = document.querySelector(`#${type}Progress .progress-bar`);
    progressBar.style.width = percent + '%';
}

// 单文件上传
async function uploadFile() {
    const fileInput = document.getElementById('fileInput');
    const resultDiv = document.getElementById('uploadResult');

    if (!fileInput.files[0]) {
        resultDiv.className = 'bg-red-100 border border-red-400 text-red-700 p-4 rounded-xl animate-fade-in';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请选择文件</span>
            </div>
        `;
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    const fileName = fileInput.files[0].name;

    try {
        showProgress('single', true);
        resultDiv.className = 'bg-blue-100 border border-blue-400 text-blue-700 p-4 rounded-xl animate-pulse';
        resultDiv.innerHTML = '🔄 正在上传文件...';

        // 模拟进度更新
        let progress = 0;
        const progressInterval = setInterval(() => {
            progress += Math.random() * 30;
            if (progress > 90) progress = 90;
            updateProgress('single', progress);
        }, 200);

        const response = await fetch(`${FileServer.baseUrl}/file/uploadOne`, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();

        clearInterval(progressInterval);
        updateProgress('single', 100);

        if (data.success) {
            resultDiv.className = 'bg-green-100 border border-green-400 text-green-700 p-4 rounded-xl animate-fade-in';
            resultDiv.innerHTML = `
                <div class="space-y-3">
                    <div class="flex items-center">
                        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                        <span class="font-semibold">✅ 上传成功!</span>
                    </div>
                    <div class="bg-white p-3 rounded border">
                        <div class="text-sm text-gray-600">文件Key:</div>
                        <div class="font-mono bg-gray-100 px-2 py-1 rounded text-sm break-all">${data.result}</div>
                        <button onclick="FileServerUtils.copyToClipboard('${data.result}')" class="mt-2 text-xs bg-blue-500 text-white px-2 py-1 rounded hover:bg-blue-600 transition-colors">
                            复制Key
                        </button>
                    </div>
                </div>
            `;

            setTimeout(() => showProgress('single', false), 1000);
        } else {
            showProgress('single', false);
            resultDiv.className = 'bg-red-100 border border-red-400 text-red-700 p-4 rounded-xl animate-fade-in';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <div>
                        <div class="font-semibold">❌ 上传失败</div>
                        <div class="text-sm mt-1">错误信息: ${data.message}</div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        showProgress('single', false);
        resultDiv.className = 'bg-red-100 border border-red-400 text-red-700 p-4 rounded-xl animate-fade-in';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <div>
                    <div class="font-semibold">❌ 上传失败</div>
                    <div class="text-sm mt-1">网络错误: ${error.message}</div>
                </div>
            </div>
        `;
    }
}

// 多文件上传
async function uploadMultipleFiles() {
    const fileInput = document.getElementById('multiFileInput');
    const resultDiv = document.getElementById('multiUploadResult');

    if (!fileInput.files.length) {
        resultDiv.className = 'bg-red-100 border border-red-400 text-red-700 p-4 rounded-xl animate-fade-in';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="font-semibold">❌ 请选择文件</span>
            </div>
        `;
        return;
    }

    const formData = new FormData();
    const fileNames = [];
    for (let file of fileInput.files) {
        formData.append('file', file);
        fileNames.push(file.name);
    }

    try {
        showProgress('multi', true);
        resultDiv.className = 'bg-blue-100 border border-blue-400 text-blue-700 p-4 rounded-xl animate-pulse';
        resultDiv.innerHTML = `🔄 正在上传 ${fileInput.files.length} 个文件...`;

        // 模拟进度更新
        let progress = 0;
        const progressInterval = setInterval(() => {
            progress += Math.random() * 20;
            if (progress > 90) progress = 90;
            updateProgress('multi', progress);
        }, 300);

        const response = await fetch(`${FileServer.baseUrl}/file/uploadFileMore`, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();

        clearInterval(progressInterval);
        updateProgress('multi', 100);

        if (data.success) {
            const fileKeys = data.result.split(',');
            resultDiv.className = 'bg-green-100 border border-green-400 text-green-700 p-4 rounded-xl animate-fade-in';

            let keysHtml = '';
            fileKeys.forEach((key, index) => {
                keysHtml += `
                    <div class="bg-white p-2 rounded border mb-2">
                        <div class="flex justify-between items-center">
                            <div>
                                <div class="text-sm font-medium">${fileNames[index] || 'Unknown'}</div>
                                <div class="font-mono text-xs text-gray-600">${key}</div>
                            </div>
                            <button onclick="FileServerUtils.copyToClipboard('${key}')" class="text-xs bg-blue-500 text-white px-2 py-1 rounded hover:bg-blue-600 transition-colors">
                                复制
                            </button>
                        </div>
                    </div>
                `;
            });

            resultDiv.innerHTML = `
                <div class="space-y-2">
                    <div class="flex items-center">
                        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                        <span class="font-semibold">✅ 上传成功! (${fileKeys.length} 个文件)</span>
                    </div>
                    <div class="max-h-40 overflow-y-auto">
                        ${keysHtml}
                    </div>
                </div>
            `;

            setTimeout(() => showProgress('multi', false), 1000);
        } else {
            showProgress('multi', false);
            resultDiv.className = 'bg-red-100 border border-red-400 text-red-700 p-4 rounded-xl animate-fade-in';
            resultDiv.innerHTML = `
                <div class="flex items-center">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <div>
                        <div class="font-semibold">❌ 上传失败</div>
                        <div class="text-sm mt-1">错误信息: ${data.message}</div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        showProgress('multi', false);
        resultDiv.className = 'bg-red-100 border border-red-400 text-red-700 p-4 rounded-xl animate-fade-in';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <div>
                    <div class="font-semibold">❌ 上传失败</div>
                    <div class="text-sm mt-1">网络错误: ${error.message}</div>
                </div>
            </div>
        `;
    }
}
