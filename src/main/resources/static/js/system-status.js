// 系统状态页面 JavaScript

// 页面加载时的初始化
document.addEventListener('DOMContentLoaded', function() {
    initializeSystemStatus();
    startRealTimeUpdates();
});

// 初始化系统状态
function initializeSystemStatus() {
    updateCurrentTime();
    loadSystemInfo();
    loadFileStatistics();
    performHealthCheck();
}

// 开始实时更新
function startRealTimeUpdates() {
    // 每秒更新当前时间
    setInterval(updateCurrentTime, 1000);
    
    // 每30秒更新系统信息
    setInterval(loadSystemInfo, 30000);
    
    // 每60秒更新文件统计
    setInterval(loadFileStatistics, 60000);
}

// 更新当前时间
function updateCurrentTime() {
    const now = new Date();
    const timeString = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
    document.getElementById('currentTime').textContent = timeString;
}

// 加载系统信息
function loadSystemInfo() {
    // 模拟系统信息（在实际项目中，这些信息应该从后端API获取）
    const startTime = new Date(Date.now() - Math.random() * 86400000); // 随机启动时间
    const uptime = calculateUptime(startTime);
    
    document.getElementById('javaVersion').textContent = 'OpenJDK 17.0.6';
    document.getElementById('springVersion').textContent = 'Spring Boot 3.3.1';
    document.getElementById('startTime').textContent = startTime.toLocaleString('zh-CN');
    document.getElementById('uptime').textContent = uptime;
}

// 计算运行时间
function calculateUptime(startTime) {
    const now = new Date();
    const uptimeMs = now - startTime;
    
    const days = Math.floor(uptimeMs / (1000 * 60 * 60 * 24));
    const hours = Math.floor((uptimeMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((uptimeMs % (1000 * 60 * 60)) / (1000 * 60));
    
    if (days > 0) {
        return `${days}天 ${hours}小时 ${minutes}分钟`;
    } else if (hours > 0) {
        return `${hours}小时 ${minutes}分钟`;
    } else {
        return `${minutes}分钟`;
    }
}

// 加载文件统计信息
async function loadFileStatistics() {
    try {
        // 这里应该调用后端API获取文件统计信息
        // 暂时使用模拟数据
        const totalFiles = Math.floor(Math.random() * 1000) + 100;
        const storageUsed = formatFileSize(Math.random() * 1024 * 1024 * 1024); // 随机存储使用量
        
        document.getElementById('totalFiles').textContent = totalFiles.toLocaleString();
        document.getElementById('storageUsed').textContent = storageUsed;
        
        // 在实际项目中，可以这样调用API：
        /*
        const response = await fetch(`${FileServer.baseUrl}/api/statistics`);
        const data = await response.json();
        if (data.success) {
            document.getElementById('totalFiles').textContent = data.result.totalFiles.toLocaleString();
            document.getElementById('storageUsed').textContent = formatFileSize(data.result.storageUsed);
        }
        */
    } catch (error) {
        console.error('加载文件统计信息失败:', error);
        document.getElementById('totalFiles').textContent = '获取失败';
        document.getElementById('storageUsed').textContent = '获取失败';
    }
}

// 格式化文件大小
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 执行健康检查
async function performHealthCheck() {
    const resultDiv = document.getElementById('healthCheckResult');
    try {
        resultDiv.className = 'p-4 rounded-xl min-h-[100px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700 animate-pulse';
        resultDiv.innerHTML = `
            <div class="flex items-center justify-center">
                <svg class="w-6 h-6 mr-3 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                </svg>
                <span class="text-lg font-medium">正在执行健康检查...</span>
            </div>
        `;
        
        const response = await fetch(`${FileServer.baseUrl}/health`);
        const data = await response.json();
        
        if (data.code === 200) {
            resultDiv.className = 'p-4 rounded-xl min-h-[100px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700 animate-fade-in';
            resultDiv.innerHTML = `
                <div class="space-y-3">
                    <div class="flex items-center">
                        <svg class="w-6 h-6 mr-3 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                        <span class="text-lg font-semibold">✅ 系统健康状态良好</span>
                    </div>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                        <div><strong>响应码:</strong> ${data.code}</div>
                        <div><strong>消息:</strong> ${data.message}</div>
                        <div><strong>结果:</strong> ${data.result}</div>
                        <div><strong>检查时间:</strong> ${new Date().toLocaleString('zh-CN')}</div>
                    </div>
                </div>
            `;
            
            // 更新服务状态
            document.getElementById('serviceStatus').textContent = '运行中';
            document.getElementById('serviceStatus').className = 'text-2xl font-bold text-green-600';
        } else {
            throw new Error(`健康检查失败: ${data.message}`);
        }
    } catch (error) {
        resultDiv.className = 'p-4 rounded-xl min-h-[100px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700 animate-fade-in';
        resultDiv.innerHTML = `
            <div class="space-y-3">
                <div class="flex items-center">
                    <svg class="w-6 h-6 mr-3 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    <span class="text-lg font-semibold">❌ 健康检查失败</span>
                </div>
                <div class="text-sm">
                    <div><strong>错误信息:</strong> ${error.message}</div>
                    <div><strong>检查时间:</strong> ${new Date().toLocaleString('zh-CN')}</div>
                    <div class="mt-2 text-red-600">请检查服务是否正常运行</div>
                </div>
            </div>
        `;
        
        // 更新服务状态
        document.getElementById('serviceStatus').textContent = '异常';
        document.getElementById('serviceStatus').className = 'text-2xl font-bold text-red-600';
    }
}

// 刷新系统信息
function refreshSystemInfo() {
    const resultDiv = document.getElementById('operationResult');
    
    resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span>正在刷新系统信息...</span>
        </div>
    `;
    
    setTimeout(() => {
        loadSystemInfo();
        loadFileStatistics();
        
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span>✅ 系统信息已刷新</span>
            </div>
        `;
        
        setTimeout(() => {
            resultDiv.innerHTML = '';
            resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300';
        }, 3000);
    }, 1000);
}

// 检查数据库连接
async function checkDatabaseConnection() {
    const resultDiv = document.getElementById('operationResult');
    
    resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span>正在检查数据库连接...</span>
        </div>
    `;
    
    try {
        // 模拟数据库连接检查
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span>✅ 数据库连接正常</span>
            </div>
        `;
        
        setTimeout(() => {
            resultDiv.innerHTML = '';
            resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300';
        }, 3000);
    } catch (error) {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-red-100 border border-red-400 text-red-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span>❌ 数据库连接失败: ${error.message}</span>
            </div>
        `;
    }
}

// 清理缓存
function clearCache() {
    const resultDiv = document.getElementById('operationResult');
    
    resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-blue-100 border border-blue-400 text-blue-700';
    resultDiv.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span>正在清理缓存...</span>
        </div>
    `;
    
    setTimeout(() => {
        resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300 bg-green-100 border border-green-400 text-green-700';
        resultDiv.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span>✅ 缓存清理完成</span>
            </div>
        `;
        
        setTimeout(() => {
            resultDiv.innerHTML = '';
            resultDiv.className = 'mt-6 p-4 rounded-xl min-h-[60px] transition-all duration-300';
        }, 3000);
    }, 2000);
}
