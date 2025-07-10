// 系统状态页面 JavaScript

// 页面加载时的初始化
document.addEventListener('DOMContentLoaded', function() {

    // 初始化系统状态
    initializeSystemStatus();

    // 开始实时更新
    startRealTimeUpdates();

    // 监听网络状态
    window.addEventListener('online', () => updateStatusIndicator('online'));
    window.addEventListener('offline', () => updateStatusIndicator('offline'));
});

// 初始化系统状态
function initializeSystemStatus() {
    loadSystemOverview();
    loadSystemInfo();
    loadMonitorData();
}

// 开始实时更新
function startRealTimeUpdates() {
    // 每5秒更新系统概览
    setInterval(loadSystemOverview, 5000);

    // 每10秒更新系统信息
    setInterval(loadSystemInfo, 10000);

    // 每30秒更新完整监控数据
    setInterval(loadMonitorData, 30000);
}



// 更新最后更新时间
function updateLastUpdateTime() {
    const now = new Date();
    const timeString = now.toLocaleString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
    const lastUpdateElement = document.getElementById('lastUpdateTime');
    if (lastUpdateElement) {
        lastUpdateElement.textContent = timeString;
    }
}

// 加载系统信息
async function loadSystemInfo() {
    try {
        const response = await fetch(`${FileServer.baseUrl}/api/system/info`);
        const data = await response.json();

        if (data.code === 0 && data.result) {
            const systemInfo = data.result;

            // 更新系统信息显示
            const startTimeElement = document.getElementById('startTime');
            const uptimeElement = document.getElementById('uptime');

            if (startTimeElement) {
                startTimeElement.textContent = systemInfo.startTime || 'Unknown';
            }
            if (uptimeElement) {
                uptimeElement.textContent = systemInfo.uptime || 'Unknown';
            }

            // 更新JVM信息
            updateJVMInfo(systemInfo);
        } else {
            // 如果API调用失败，显示错误信息
            const startTimeElement = document.getElementById('startTime');
            const uptimeElement = document.getElementById('uptime');

            if (startTimeElement) {
                startTimeElement.textContent = 'API调用失败';
            }
            if (uptimeElement) {
                uptimeElement.textContent = 'API调用失败';
            }

            // 更新JVM信息为失败状态
            updateJVMInfo(null);
        }
    } catch (error) {
        console.error('加载系统信息失败:', error);
        // 如果网络错误，显示错误信息
        const startTimeElement = document.getElementById('startTime');
        const uptimeElement = document.getElementById('uptime');

        if (startTimeElement) {
            startTimeElement.textContent = '网络错误';
        }
        if (uptimeElement) {
            uptimeElement.textContent = '网络错误';
        }

        // 更新JVM信息为错误状态
        updateJVMInfo(null);
    }
}



// 加载系统概览数据
async function loadSystemOverview() {
    try {
        const response = await fetch(`${FileServer.baseUrl}/api/system/overview`);
        const data = await response.json();

        if (data.code === 0 && data.result) {
            const overview = data.result;

            // 更新服务状态
            updateServiceStatus(overview.serviceStatus || '运行中', overview.serviceStatusCode || 1, overview.uptime);

            // 更新内存使用
            updateMemoryStatus(overview.memoryUsagePercent || 0, overview.usedMemory || '0 MB', overview.totalMemory || '0 MB');

            // 更新最后更新时间
            updateLastUpdateTime();

        } else {
            console.error('获取系统概览失败:', data.message);
        }
    } catch (error) {
        console.error('加载系统概览失败:', error);
        // 设置错误状态
        updateServiceStatus('异常', 0);
    }
}

// 更新服务状态显示
function updateServiceStatus(status, statusCode, uptime) {
    const serviceStatusElement = document.getElementById('serviceStatus');
    const statusIndicator = document.getElementById('statusIndicator');
    const statusDot = document.getElementById('statusDot');
    const statusIcon = document.getElementById('statusIcon');
    const statusIconContainer = document.getElementById('statusIconContainer');
    const statusBadge = document.getElementById('statusBadge');
    const statusDescription = document.getElementById('statusDescription');
    const statusRipple = document.getElementById('statusRipple');
    const statusUptime = document.getElementById('statusUptime');

    if (statusCode === 1) {
        // 正常状态
        if (serviceStatusElement) {
            serviceStatusElement.textContent = status;
            serviceStatusElement.className = 'text-xl font-bold text-green-600';
        }

        if (statusIndicator) {
            statusIndicator.className = 'w-3 h-3 bg-green-500 rounded-full animate-pulse shadow-lg';
        }

        if (statusDot) {
            statusDot.textContent = '在线';
            statusDot.className = 'text-xs text-green-500';
        }

        if (statusIcon) {
            statusIcon.textContent = '🚀';
            statusIcon.className = 'text-3xl animate-bounce';
        }

        if (statusIconContainer) {
            const container = statusIconContainer.querySelector('div');
            if (container) {
                container.className = 'w-16 h-16 bg-gradient-to-br from-green-100 to-green-200 rounded-2xl flex items-center justify-center transform transition-all duration-300 hover:scale-105';
            }
        }

        if (statusBadge) {
            statusBadge.textContent = 'ONLINE';
            statusBadge.className = 'px-2 py-1 bg-green-100 text-green-700 text-xs font-medium rounded-full';
        }

        if (statusDescription) {
            statusDescription.textContent = '系统运行正常，所有服务可用';
        }

        if (statusRipple) {
            statusRipple.className = 'absolute inset-0 rounded-2xl bg-green-400 opacity-20 animate-ping';
        }

    } else {
        // 异常状态
        if (serviceStatusElement) {
            serviceStatusElement.textContent = status;
            serviceStatusElement.className = 'text-xl font-bold text-red-600';
        }

        if (statusIndicator) {
            statusIndicator.className = 'w-3 h-3 bg-red-500 rounded-full animate-pulse shadow-lg';
        }

        if (statusDot) {
            statusDot.textContent = '离线';
            statusDot.className = 'text-xs text-red-500';
        }

        if (statusIcon) {
            statusIcon.textContent = '⚠️';
            statusIcon.className = 'text-3xl animate-pulse';
        }

        if (statusIconContainer) {
            const container = statusIconContainer.querySelector('div');
            if (container) {
                container.className = 'w-16 h-16 bg-gradient-to-br from-red-100 to-red-200 rounded-2xl flex items-center justify-center transform transition-all duration-300 hover:scale-105';
            }
        }

        if (statusBadge) {
            statusBadge.textContent = 'OFFLINE';
            statusBadge.className = 'px-2 py-1 bg-red-100 text-red-700 text-xs font-medium rounded-full';
        }

        if (statusDescription) {
            statusDescription.textContent = '系统服务异常，请检查系统状态';
        }

        if (statusRipple) {
            statusRipple.className = 'absolute inset-0 rounded-2xl bg-red-400 opacity-20 animate-ping';
        }
    }

    // 更新运行时间
    if (statusUptime && uptime) {
        statusUptime.textContent = `运行时间: ${uptime}`;
    } else if (statusUptime) {
        statusUptime.textContent = '运行时间: 计算中...';
    }
}

// 更新JVM信息显示
function updateJVMInfo(systemInfo) {
    const springBootVersionElement = document.getElementById('springBootVersion');
    const jvmRuntimeElement = document.getElementById('jvmRuntime');

    if (systemInfo) {
        // 更新Spring Boot版本
        if (springBootVersionElement) {
            springBootVersionElement.textContent = systemInfo.springBootVersion || 'Unknown';
        }

        // 更新JVM运行状态
        if (jvmRuntimeElement) {
            jvmRuntimeElement.textContent = '运行正常';
            jvmRuntimeElement.className = 'font-semibold text-green-700';
        }
    } else {
        // 错误状态
        if (springBootVersionElement) {
            springBootVersionElement.textContent = '获取失败';
        }

        if (jvmRuntimeElement) {
            jvmRuntimeElement.textContent = '状态异常';
            jvmRuntimeElement.className = 'font-semibold text-red-700';
        }
    }
}

// 更新内存状态显示
function updateMemoryStatus(usagePercent, usedMemory, totalMemory) {
    const memoryUsagePercentElement = document.getElementById('memoryUsagePercent');
    const memoryIndicator = document.getElementById('memoryIndicator');
    const memoryStatus = document.getElementById('memoryStatus');
    const memoryIcon = document.getElementById('memoryIcon');
    const memoryIconContainer = document.getElementById('memoryIconContainer');
    const memoryBadge = document.getElementById('memoryBadge');
    const memoryDescription = document.getElementById('memoryDescription');
    const memoryRipple = document.getElementById('memoryRipple');
    const memoryUsageDetail = document.getElementById('memoryUsageDetail');
    const memoryProgressBar = document.getElementById('memoryProgressBar');

    // 更新基本信息
    if (memoryUsagePercentElement) {
        memoryUsagePercentElement.textContent = usagePercent + '%';
    }

    if (memoryUsageDetail) {
        memoryUsageDetail.textContent = `${usedMemory} / ${totalMemory}`;
    }

    if (memoryProgressBar) {
        memoryProgressBar.style.width = usagePercent + '%';
    }

    // 根据内存使用率设置不同的状态
    if (usagePercent < 70) {
        // 正常状态 (< 70%)
        if (memoryUsagePercentElement) {
            memoryUsagePercentElement.className = 'text-xl font-bold text-green-600';
        }

        if (memoryIndicator) {
            memoryIndicator.className = 'w-3 h-3 bg-green-500 rounded-full animate-pulse shadow-lg';
        }

        if (memoryStatus) {
            memoryStatus.textContent = '正常';
            memoryStatus.className = 'text-xs text-green-500';
        }

        if (memoryIcon) {
            memoryIcon.textContent = '🧠';
            memoryIcon.className = 'text-3xl animate-pulse';
        }

        if (memoryIconContainer) {
            const container = memoryIconContainer.querySelector('div');
            if (container) {
                container.className = 'w-16 h-16 bg-gradient-to-br from-green-100 to-green-200 rounded-2xl flex items-center justify-center transform transition-all duration-300 hover:scale-105';
            }
        }

        if (memoryBadge) {
            memoryBadge.textContent = 'NORMAL';
            memoryBadge.className = 'px-2 py-1 bg-green-100 text-green-700 text-xs font-medium rounded-full';
        }

        if (memoryDescription) {
            memoryDescription.textContent = '内存使用正常';
        }

        if (memoryRipple) {
            memoryRipple.className = 'absolute inset-0 rounded-2xl bg-green-400 opacity-20 animate-ping';
        }

        if (memoryProgressBar) {
            memoryProgressBar.className = 'bg-gradient-to-r from-green-400 to-green-500 h-2 rounded-full transition-all duration-500';
        }

    } else if (usagePercent < 85) {
        // 警告状态 (70% - 85%)
        if (memoryUsagePercentElement) {
            memoryUsagePercentElement.className = 'text-xl font-bold text-yellow-600';
        }

        if (memoryIndicator) {
            memoryIndicator.className = 'w-3 h-3 bg-yellow-500 rounded-full animate-pulse shadow-lg';
        }

        if (memoryStatus) {
            memoryStatus.textContent = '警告';
            memoryStatus.className = 'text-xs text-yellow-500';
        }

        if (memoryIcon) {
            memoryIcon.textContent = '⚠️';
            memoryIcon.className = 'text-3xl animate-bounce';
        }

        if (memoryIconContainer) {
            const container = memoryIconContainer.querySelector('div');
            if (container) {
                container.className = 'w-16 h-16 bg-gradient-to-br from-yellow-100 to-yellow-200 rounded-2xl flex items-center justify-center transform transition-all duration-300 hover:scale-105';
            }
        }

        if (memoryBadge) {
            memoryBadge.textContent = 'WARNING';
            memoryBadge.className = 'px-2 py-1 bg-yellow-100 text-yellow-700 text-xs font-medium rounded-full';
        }

        if (memoryDescription) {
            memoryDescription.textContent = '内存使用率较高，建议关注';
        }

        if (memoryRipple) {
            memoryRipple.className = 'absolute inset-0 rounded-2xl bg-yellow-400 opacity-20 animate-ping';
        }

        if (memoryProgressBar) {
            memoryProgressBar.className = 'bg-gradient-to-r from-yellow-400 to-yellow-500 h-2 rounded-full transition-all duration-500';
        }

    } else {
        // 危险状态 (>= 85%)
        if (memoryUsagePercentElement) {
            memoryUsagePercentElement.className = 'text-xl font-bold text-red-600';
        }

        if (memoryIndicator) {
            memoryIndicator.className = 'w-3 h-3 bg-red-500 rounded-full animate-pulse shadow-lg';
        }

        if (memoryStatus) {
            memoryStatus.textContent = '危险';
            memoryStatus.className = 'text-xs text-red-500';
        }

        if (memoryIcon) {
            memoryIcon.textContent = '🔥';
            memoryIcon.className = 'text-3xl animate-bounce';
        }

        if (memoryIconContainer) {
            const container = memoryIconContainer.querySelector('div');
            if (container) {
                container.className = 'w-16 h-16 bg-gradient-to-br from-red-100 to-red-200 rounded-2xl flex items-center justify-center transform transition-all duration-300 hover:scale-105';
            }
        }

        if (memoryBadge) {
            memoryBadge.textContent = 'CRITICAL';
            memoryBadge.className = 'px-2 py-1 bg-red-100 text-red-700 text-xs font-medium rounded-full';
        }

        if (memoryDescription) {
            memoryDescription.textContent = '内存使用率过高，需要立即处理';
        }

        if (memoryRipple) {
            memoryRipple.className = 'absolute inset-0 rounded-2xl bg-red-400 opacity-20 animate-ping';
        }

        if (memoryProgressBar) {
            memoryProgressBar.className = 'bg-gradient-to-r from-red-400 to-red-500 h-2 rounded-full transition-all duration-500';
        }
    }
}

// 加载完整监控数据
async function loadMonitorData() {
    try {
        const response = await fetch(`${FileServer.baseUrl}/api/system/monitor`);
        const data = await response.json();

        if (data.code === 0 && data.result) {
            const monitorData = data.result;

            // 更新详细的系统信息
            console.log('监控数据更新:', monitorData);

            // 可以在这里添加更多详细信息的更新
            // 比如CPU使用率、磁盘使用率等

        } else {
            console.error('获取监控数据失败:', data.message);
        }
    } catch (error) {
        console.error('加载监控数据失败:', error);
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

// 更新元素文本内容的辅助函数
function updateElementText(elementId, value) {
    const element = document.getElementById(elementId);
    if (element && value !== undefined && value !== null) {
        // 添加数字动画效果
        if (typeof value === 'number' && !isNaN(value)) {
            animateNumber(element, parseInt(element.textContent) || 0, value);
        } else {
            element.textContent = value;
        }
    }
}

// 数字动画效果
function animateNumber(element, start, end, duration = 1000) {
    if (start === end) return;

    const range = end - start;
    const increment = range / (duration / 16); // 60fps
    let current = start;

    const timer = setInterval(() => {
        current += increment;
        if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
            current = end;
            clearInterval(timer);
        }
        element.textContent = Math.round(current);
    }, 16);
}




