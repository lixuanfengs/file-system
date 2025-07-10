// 系统状态SSE实时监控页面 JavaScript

// 全局变量
let eventSources = {};
let isConnected = false;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;

// Chart.js 相关变量
let cpuCoreChart = null;
let chartDataHistory = [];
const maxDataPoints = 60; // 保留最近60个数据点（约2分钟的数据）
const chartColors = [
    '#3B82F6', '#EF4444', '#10B981', '#F59E0B', '#8B5CF6', '#06B6D4',
    '#F97316', '#84CC16', '#EC4899', '#6366F1', '#14B8A6', '#F59E0B',
    '#8B5CF6', '#06B6D4', '#F97316', '#84CC16'
];

// 页面加载时的初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('系统监控页面初始化...');

    // 初始化连接状态指示器
    initConnectionStatus();

    // 初始化折叠面板
    initializeCollapsibleSections();

    // 初始化CPU核心图表
    initializeCpuCoreChart();

    // 启动SSE连接
    startSystemMonitoring();

    // 监听页面可见性变化
    document.addEventListener('visibilitychange', handleVisibilityChange);

    // 页面卸载时清理连接
    window.addEventListener('beforeunload', cleanup);
});

/**
 * 初始化连接状态指示器
 */
function initConnectionStatus() {
    const statusElement = document.getElementById('connectionStatus');
    if (statusElement) {
        statusElement.classList.add('hidden');
    }
}

/**
 * 启动系统监控
 */
function startSystemMonitoring() {
    console.log('启动系统监控...');
    
    // 启动主监控流
    startMainMonitorStream();
    
    // 启动CPU监控流
    startCpuMonitorStream();
    
    // 启动内存监控流
    startMemoryMonitorStream();
    
    // 启动磁盘监控流
    startDiskMonitorStream();
    
    // 启动网络监控流
    startNetworkMonitorStream();
    

}

/**
 * 启动主监控流
 */
function startMainMonitorStream() {
    const eventSource = new EventSource(`${FileServer.baseUrl}/api/system/monitor/stream`);
    eventSources.main = eventSource;
    
    eventSource.onopen = function(event) {
        console.log('主监控流连接成功');
        updateConnectionStatus(true);
        reconnectAttempts = 0;
    };
    
    eventSource.onmessage = function(event) {
        try {
            const data = JSON.parse(event.data);
            if (data.error) {
                console.error('主监控数据错误:', data.message);
                return;
            }
            
            // 更新系统信息
            updateSystemInfo(data);

            // 更新硬件信息
            updateHardwareInfo(data);
            
        } catch (error) {
            console.error('解析主监控数据失败:', error);
        }
    };
    
    eventSource.onerror = function(event) {
        console.error('主监控流连接错误');
        updateConnectionStatus(false);
        handleReconnect('main', startMainMonitorStream);
    };
}

/**
 * 启动CPU监控流
 */
function startCpuMonitorStream() {
    const eventSource = new EventSource(`${FileServer.baseUrl}/api/system/monitor/cpu/stream`);
    eventSources.cpu = eventSource;

    eventSource.onopen = function(event) {
        console.log('CPU监控流连接成功');
    };

    eventSource.onmessage = function(event) {
        try {
            const data = JSON.parse(event.data);
            if (data.error) {
                console.error('CPU监控数据错误:', data.message);
                showError('CPU监控数据获取失败');
                return;
            }

            updateCpuInfo(data);
            updateCpuCores(data.coreUsages);

        } catch (error) {
            console.error('解析CPU监控数据失败:', error);
        }
    };

    eventSource.onerror = function(event) {
        console.error('CPU监控流连接错误');
        handleReconnect('cpu', startCpuMonitorStream);
    };
}

/**
 * 启动内存监控流
 */
function startMemoryMonitorStream() {
    const eventSource = new EventSource(`${FileServer.baseUrl}/api/system/monitor/memory/stream`);
    eventSources.memory = eventSource;
    
    eventSource.onmessage = function(event) {
        try {
            const data = JSON.parse(event.data);
            if (data.error) {
                console.error('内存监控数据错误:', data.message);
                return;
            }
            
            updateMemoryInfo(data);
            
        } catch (error) {
            console.error('解析内存监控数据失败:', error);
        }
    };
    
    eventSource.onerror = function(event) {
        console.error('内存监控流连接错误');
        handleReconnect('memory', startMemoryMonitorStream);
    };
}

/**
 * 启动磁盘监控流
 */
function startDiskMonitorStream() {
    const eventSource = new EventSource(`${FileServer.baseUrl}/api/system/monitor/disk/stream`);
    eventSources.disk = eventSource;
    
    eventSource.onmessage = function(event) {
        try {
            const data = JSON.parse(event.data);
            if (data.error) {
                console.error('磁盘监控数据错误:', data.message);
                return;
            }
            
            updateDiskInfo(data);
            updateDiskDetails(data.disks);
            
        } catch (error) {
            console.error('解析磁盘监控数据失败:', error);
        }
    };
    
    eventSource.onerror = function(event) {
        console.error('磁盘监控流连接错误');
        handleReconnect('disk', startDiskMonitorStream);
    };
}

/**
 * 启动网络监控流
 */
function startNetworkMonitorStream() {
    const eventSource = new EventSource(`${FileServer.baseUrl}/api/system/monitor/network/stream`);
    eventSources.network = eventSource;
    
    eventSource.onmessage = function(event) {
        try {
            const data = JSON.parse(event.data);
            if (data.error) {
                console.error('网络监控数据错误:', data.message);
                return;
            }
            
            updateNetworkInfo(data);
            
        } catch (error) {
            console.error('解析网络监控数据失败:', error);
        }
    };
    
    eventSource.onerror = function(event) {
        console.error('网络监控流连接错误');
        handleReconnect('network', startNetworkMonitorStream);
    };
}



/**
 * 更新连接状态
 */
function updateConnectionStatus(connected) {
    isConnected = connected;
    const statusElement = document.getElementById('connectionStatus');
    
    if (statusElement) {
        if (connected) {
            statusElement.className = 'mb-6 p-4 rounded-lg border-l-4 border-green-500 bg-green-50';
            statusElement.innerHTML = `
                <div class="flex items-center">
                    <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse mr-3"></div>
                    <span class="text-green-700 font-medium">实时监控已连接</span>
                </div>
            `;
            statusElement.classList.remove('hidden');
        } else {
            statusElement.className = 'mb-6 p-4 rounded-lg border-l-4 border-red-500 bg-red-50';
            statusElement.innerHTML = `
                <div class="flex items-center">
                    <div class="w-3 h-3 bg-red-500 rounded-full mr-3"></div>
                    <span class="text-red-700 font-medium">连接已断开，正在重连...</span>
                </div>
            `;
            statusElement.classList.remove('hidden');
        }
    }
}

/**
 * 处理重连
 */
function handleReconnect(streamName, restartFunction) {
    if (reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        console.log(`尝试重连 ${streamName} 流，第 ${reconnectAttempts} 次`);
        
        setTimeout(() => {
            if (eventSources[streamName]) {
                eventSources[streamName].close();
            }
            restartFunction();
        }, 2000 * reconnectAttempts); // 递增延迟
    } else {
        console.error(`${streamName} 流重连失败，已达到最大重试次数`);
        showError(`${streamName} 监控连接失败，请刷新页面重试`);
    }
}

/**
 * 处理页面可见性变化
 */
function handleVisibilityChange() {
    if (document.hidden) {
        console.log('页面隐藏，暂停监控');
        // 可以选择暂停某些监控流以节省资源
    } else {
        console.log('页面可见，恢复监控');
        // 检查连接状态，必要时重新连接
        if (!isConnected) {
            cleanup();
            startSystemMonitoring();
        }
    }
}

/**
 * 清理所有连接
 */
function cleanup() {
    console.log('清理SSE连接...');
    Object.keys(eventSources).forEach(key => {
        if (eventSources[key]) {
            eventSources[key].close();
            delete eventSources[key];
        }
    });

    // 清理Chart.js资源
    if (cpuCoreChart) {
        cpuCoreChart.destroy();
        cpuCoreChart = null;
    }

    isConnected = false;
}

/**
 * 显示错误信息
 */
function showError(message) {
    // 使用全局的toast显示错误
    if (window.FileServerUtils && window.FileServerUtils.showToast) {
        window.FileServerUtils.showToast(message, 'error');
    } else {
        console.error(message);
    }
}

// ==================== Chart.js 图表功能 ====================

/**
 * 初始化CPU核心使用率图表
 */
function initializeCpuCoreChart() {
    const ctx = document.getElementById('cpuCoreChart');
    if (!ctx) return;

    cpuCoreChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [], // 时间标签
            datasets: [] // CPU核心数据集，将动态添加
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false,
            },
            plugins: {
                title: {
                    display: true,
                    text: 'CPU核心使用率实时监控',
                    font: {
                        size: 16,
                        weight: 'bold'
                    }
                },
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 15,
                        font: {
                            size: 11
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleColor: 'white',
                    bodyColor: 'white',
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    borderWidth: 1,
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${context.parsed.y.toFixed(1)}%`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    display: true,
                    title: {
                        display: true,
                        text: '时间'
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)'
                    }
                },
                y: {
                    display: true,
                    title: {
                        display: true,
                        text: 'CPU使用率 (%)'
                    },
                    min: 0,
                    max: 100,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)'
                    },
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            },
            elements: {
                line: {
                    tension: 0.2, // 平滑曲线
                    borderWidth: 2
                },
                point: {
                    radius: 0, // 隐藏数据点
                    hoverRadius: 4
                }
            },
            animation: {
                duration: 0 // 禁用动画以提高性能
            }
        }
    });

    // 添加重置按钮事件
    const resetBtn = document.getElementById('resetChartBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetCpuChart);
    }
}

/**
 * 更新CPU核心图表数据
 */
function updateCpuCoreChart(coreUsages) {
    if (!cpuCoreChart || !coreUsages || coreUsages.length === 0) return;

    const currentTime = new Date().toLocaleTimeString();

    // 如果是第一次添加数据，初始化数据集
    if (cpuCoreChart.data.datasets.length === 0) {
        coreUsages.forEach((usage, index) => {
            cpuCoreChart.data.datasets.push({
                label: `核心 ${index + 1}`,
                data: [],
                borderColor: chartColors[index % chartColors.length],
                backgroundColor: chartColors[index % chartColors.length] + '20',
                fill: false,
                tension: 0.2
            });
        });
    }

    // 添加新的时间标签
    cpuCoreChart.data.labels.push(currentTime);

    // 为每个CPU核心添加新数据
    coreUsages.forEach((usage, index) => {
        if (cpuCoreChart.data.datasets[index]) {
            cpuCoreChart.data.datasets[index].data.push(usage);
        }
    });

    // 限制数据点数量，移除旧数据
    if (cpuCoreChart.data.labels.length > maxDataPoints) {
        cpuCoreChart.data.labels.shift();
        cpuCoreChart.data.datasets.forEach(dataset => {
            dataset.data.shift();
        });
    }

    // 更新图表
    cpuCoreChart.update('none'); // 使用 'none' 模式避免动画

    // 更新数据点计数
    updateChartDataPointsCount();
}

/**
 * 重置CPU图表
 */
function resetCpuChart() {
    if (!cpuCoreChart) return;

    cpuCoreChart.data.labels = [];
    cpuCoreChart.data.datasets.forEach(dataset => {
        dataset.data = [];
    });
    cpuCoreChart.update();

    updateChartDataPointsCount();
}

/**
 * 更新图表数据点计数显示
 */
function updateChartDataPointsCount() {
    const countElement = document.getElementById('chartDataPoints');
    if (countElement && cpuCoreChart) {
        countElement.textContent = cpuCoreChart.data.labels.length;
    }
}

// ==================== 折叠功能 ====================

/**
 * 切换面板折叠状态
 */
function toggleSection(sectionId) {
    const content = document.getElementById(sectionId + 'Content');
    const toggle = document.getElementById(sectionId + 'Toggle');

    if (!content || !toggle) return;

    const isHidden = content.classList.contains('hidden');

    if (isHidden) {
        // 展开动画
        content.classList.remove('hidden');
        content.style.maxHeight = '0px';
        content.style.opacity = '0';

        // 强制重绘
        content.offsetHeight;

        // 开始展开
        content.style.maxHeight = content.scrollHeight + 'px';
        content.style.opacity = '1';
        toggle.style.transform = 'rotate(180deg)';

        // 动画完成后移除固定高度
        setTimeout(() => {
            if (!content.classList.contains('hidden')) {
                content.style.maxHeight = 'none';
            }
        }, 300);
    } else {
        // 收起动画
        content.style.maxHeight = content.scrollHeight + 'px';
        content.style.opacity = '1';

        // 强制重绘
        content.offsetHeight;

        // 开始收起
        content.style.maxHeight = '0px';
        content.style.opacity = '0';
        toggle.style.transform = 'rotate(0deg)';

        // 动画完成后隐藏元素
        setTimeout(() => {
            content.classList.add('hidden');
            content.style.maxHeight = '';
            content.style.opacity = '';
        }, 300);
    }
}

/**
 * 初始化所有折叠面板为收起状态
 */
function initializeCollapsibleSections() {
    const sections = ['diskDetails', 'systemInfo', 'hardwareInfo'];

    sections.forEach(sectionId => {
        const content = document.getElementById(sectionId + 'Content');
        const toggle = document.getElementById(sectionId + 'Toggle');

        if (content && toggle) {
            // 确保初始状态为收起
            content.classList.add('hidden');
            content.style.maxHeight = '0px';
            content.style.opacity = '0';
            toggle.style.transform = 'rotate(0deg)';
        }
    });
}

// ==================== 数据更新函数 ====================

/**
 * 更新CPU信息
 */
function updateCpuInfo(cpuData) {
    const cpuUsageElement = document.getElementById('cpuUsage');
    const cpuProgressBar = document.getElementById('cpuProgressBar');
    const cpuInfoElement = document.getElementById('cpuInfo');

    if (cpuUsageElement) {
        cpuUsageElement.textContent = `${cpuData.usage}%`;
    }

    if (cpuProgressBar) {
        cpuProgressBar.style.width = `${cpuData.usage}%`;

        // 根据使用率改变颜色
        if (cpuData.usage > 80) {
            cpuProgressBar.className = 'bg-gradient-to-r from-red-400 to-red-500 h-2 rounded-full transition-all duration-500';
        } else if (cpuData.usage > 60) {
            cpuProgressBar.className = 'bg-gradient-to-r from-yellow-400 to-yellow-500 h-2 rounded-full transition-all duration-500';
        } else {
            cpuProgressBar.className = 'bg-gradient-to-r from-blue-400 to-blue-500 h-2 rounded-full transition-all duration-500';
        }
    }

    if (cpuInfoElement) {
        cpuInfoElement.textContent = `${cpuData.physicalCores}核/${cpuData.logicalCores}线程`;
    }
}

/**
 * 更新CPU核心使用率
 */
function updateCpuCores(coreUsages) {
    // 更新Chart.js图表
    updateCpuCoreChart(coreUsages);

    // 更新网格显示
    const cpuCoresElement = document.getElementById('cpuCores');
    if (!cpuCoresElement || !coreUsages) return;

    cpuCoresElement.innerHTML = '';

    coreUsages.forEach((usage, index) => {
        const coreElement = document.createElement('div');
        coreElement.className = 'text-center p-2 bg-gradient-to-br from-gray-50 to-gray-100 rounded-lg border';

        let colorClass = 'text-blue-600';
        let progressColor = 'from-blue-400 to-blue-500';
        if (usage > 80) {
            colorClass = 'text-red-600';
            progressColor = 'from-red-400 to-red-500';
        } else if (usage > 60) {
            colorClass = 'text-yellow-600';
            progressColor = 'from-yellow-400 to-yellow-500';
        }

        coreElement.innerHTML = `
            <div class="text-sm font-bold ${colorClass}">${usage}%</div>
            <div class="text-xs text-gray-500 mb-1">核心${index + 1}</div>
            <div class="w-full bg-gray-200 rounded-full h-1">
                <div class="bg-gradient-to-r ${progressColor} h-1 rounded-full transition-all duration-500" style="width: ${usage}%"></div>
            </div>
        `;

        cpuCoresElement.appendChild(coreElement);
    });
}

/**
 * 更新内存信息
 */
function updateMemoryInfo(memoryData) {
    const memoryUsageElement = document.getElementById('memoryUsage');
    const memoryProgressBar = document.getElementById('memoryProgressBar');
    const memoryInfoElement = document.getElementById('memoryInfo');

    if (memoryUsageElement) {
        memoryUsageElement.textContent = `${memoryData.usagePercent}%`;
    }

    if (memoryProgressBar) {
        memoryProgressBar.style.width = `${memoryData.usagePercent}%`;

        // 根据使用率改变颜色
        if (memoryData.usagePercent > 80) {
            memoryProgressBar.className = 'bg-gradient-to-r from-red-400 to-red-500 h-2 rounded-full transition-all duration-500';
        } else if (memoryData.usagePercent > 60) {
            memoryProgressBar.className = 'bg-gradient-to-r from-yellow-400 to-yellow-500 h-2 rounded-full transition-all duration-500';
        } else {
            memoryProgressBar.className = 'bg-gradient-to-r from-green-400 to-green-500 h-2 rounded-full transition-all duration-500';
        }
    }

    if (memoryInfoElement) {
        memoryInfoElement.textContent = `${memoryData.usedFormatted} / ${memoryData.totalFormatted}`;
    }
}

/**
 * 更新磁盘信息
 */
function updateDiskInfo(diskData) {
    const diskReadSpeedElement = document.getElementById('diskReadSpeed');
    const diskWriteSpeedElement = document.getElementById('diskWriteSpeed');

    if (diskReadSpeedElement) {
        diskReadSpeedElement.textContent = diskData.totalReadSpeedFormatted || '0 B/s';
    }

    if (diskWriteSpeedElement) {
        diskWriteSpeedElement.textContent = diskData.totalWriteSpeedFormatted || '0 B/s';
    }
}

/**
 * 更新磁盘详情
 */
function updateDiskDetails(disks) {
    const diskDetailsElement = document.getElementById('diskDetailsContent');
    if (!diskDetailsElement || !disks) return;

    diskDetailsElement.innerHTML = '';

    disks.forEach(disk => {
        const diskElement = document.createElement('div');
        diskElement.className = 'p-4 bg-gradient-to-r from-yellow-50 to-yellow-100 rounded-lg border border-yellow-200';

        // 计算使用率颜色
        let usageColor = 'text-green-600';
        let progressColor = 'from-green-400 to-green-500';
        if (disk.usagePercent > 80) {
            usageColor = 'text-red-600';
            progressColor = 'from-red-400 to-red-500';
        } else if (disk.usagePercent > 60) {
            usageColor = 'text-yellow-600';
            progressColor = 'from-yellow-400 to-yellow-500';
        }

        diskElement.innerHTML = `
            <div class="flex items-center justify-between mb-3">
                <div class="font-medium text-gray-800">${disk.name || disk.mount}</div>
                <div class="text-sm ${usageColor} font-bold">${disk.usagePercent}%</div>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2 mb-3">
                <div class="bg-gradient-to-r ${progressColor} h-2 rounded-full transition-all duration-500" style="width: ${disk.usagePercent}%"></div>
            </div>
            <div class="grid grid-cols-3 gap-2 text-xs">
                <div class="text-center">
                    <div class="text-gray-500">总容量</div>
                    <div class="font-medium text-gray-700">${disk.totalSpaceFormatted}</div>
                </div>
                <div class="text-center">
                    <div class="text-gray-500">已使用</div>
                    <div class="font-medium ${usageColor}">${disk.usedSpaceFormatted}</div>
                </div>
                <div class="text-center">
                    <div class="text-gray-500">剩余</div>
                    <div class="font-medium text-green-600">${disk.freeSpaceFormatted}</div>
                </div>
            </div>
            ${disk.type ? `<div class="text-xs text-gray-400 mt-2">文件系统: ${disk.type}</div>` : ''}
        `;

        diskDetailsElement.appendChild(diskElement);
    });
}

/**
 * 更新网络信息
 */
function updateNetworkInfo(networkData) {
    const networkReceiveSpeedElement = document.getElementById('networkReceiveSpeed');
    const networkSendSpeedElement = document.getElementById('networkSendSpeed');

    if (networkReceiveSpeedElement) {
        networkReceiveSpeedElement.textContent = networkData.totalReceiveSpeedFormatted || '0 B/s';
    }

    if (networkSendSpeedElement) {
        networkSendSpeedElement.textContent = networkData.totalSendSpeedFormatted || '0 B/s';
    }
}



/**
 * 更新系统负载
 */
function updateSystemLoad(loadData) {
    const load1minElement = document.getElementById('load1min');
    const load5minElement = document.getElementById('load5min');
    const load15minElement = document.getElementById('load15min');
    const processCountElement = document.getElementById('processCount');
    const threadCountElement = document.getElementById('threadCount');

    if (load1minElement && loadData.load) {
        load1minElement.textContent = loadData.load.load1min >= 0 ? loadData.load.load1min.toFixed(2) : '-';
    }

    if (load5minElement && loadData.load) {
        load5minElement.textContent = loadData.load.load5min >= 0 ? loadData.load.load5min.toFixed(2) : '-';
    }

    if (load15minElement && loadData.load) {
        load15minElement.textContent = loadData.load.load15min >= 0 ? loadData.load.load15min.toFixed(2) : '-';
    }

    if (processCountElement) {
        processCountElement.textContent = loadData.processCount || '-';
    }

    if (threadCountElement) {
        threadCountElement.textContent = loadData.threadCount || '-';
    }
}



/**
 * 更新系统信息
 */
function updateSystemInfo(systemData) {
    const systemInfoElement = document.getElementById('systemInfoContent');
    if (!systemInfoElement || !systemData.os) return;

    const osData = systemData.os;

    systemInfoElement.innerHTML = `
        <div class="space-y-3">
            <div class="flex items-center justify-between p-3 bg-gradient-to-r from-blue-50 to-blue-100 rounded-lg">
                <span class="text-sm font-medium text-gray-700">操作系统</span>
                <span class="text-sm font-bold text-blue-700">${osData.family} ${osData.version}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gradient-to-r from-indigo-50 to-indigo-100 rounded-lg">
                <span class="text-sm font-medium text-gray-700">制造商</span>
                <span class="text-sm font-bold text-indigo-700">${osData.manufacturer}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gradient-to-r from-purple-50 to-purple-100 rounded-lg">
                <span class="text-sm font-medium text-gray-700">系统位数</span>
                <span class="text-sm font-bold text-purple-700">${osData.bitness} 位</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gradient-to-r from-green-50 to-green-100 rounded-lg">
                <span class="text-sm font-medium text-gray-700">启动时间</span>
                <span class="text-sm font-bold text-green-700">${osData.bootTimeFormatted}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gradient-to-r from-teal-50 to-teal-100 rounded-lg">
                <span class="text-sm font-medium text-gray-700">运行时间</span>
                <span class="text-sm font-bold text-teal-700">${osData.uptimeFormatted}</span>
            </div>
        </div>
    `;
}

/**
 * 更新硬件信息
 */
function updateHardwareInfo(systemData) {
    const hardwareInfoElement = document.getElementById('hardwareInfoContent');
    if (!hardwareInfoElement || !systemData.cpu) return;

    const cpuData = systemData.cpu;
    const memoryData = systemData.memory;

    hardwareInfoElement.innerHTML = `
        <div class="space-y-3">
            <div class="p-3 bg-gradient-to-r from-green-50 to-green-100 rounded-lg">
                <div class="flex items-center justify-between mb-2">
                    <span class="text-sm font-medium text-gray-700">处理器</span>
                    <span class="text-xs text-green-600">${cpuData.vendor}</span>
                </div>
                <div class="text-sm font-bold text-green-700 mb-1">${cpuData.name}</div>
                <div class="text-xs text-gray-500">
                    ${cpuData.physicalCores}核心 / ${cpuData.logicalCores}线程 @ ${cpuData.maxFreq}
                </div>
            </div>
            <div class="p-3 bg-gradient-to-r from-emerald-50 to-emerald-100 rounded-lg">
                <div class="flex items-center justify-between mb-2">
                    <span class="text-sm font-medium text-gray-700">内存</span>
                    <span class="text-xs text-emerald-600">${memoryData.usagePercent}%</span>
                </div>
                <div class="text-sm font-bold text-emerald-700 mb-1">${memoryData.totalFormatted}</div>
                <div class="text-xs text-gray-500">
                    已用: ${memoryData.usedFormatted} / 可用: ${memoryData.availableFormatted}
                </div>
            </div>
            ${memoryData.virtual && memoryData.virtual.swapTotal > 0 ? `
            <div class="p-3 bg-gradient-to-r from-teal-50 to-teal-100 rounded-lg">
                <div class="flex items-center justify-between mb-2">
                    <span class="text-sm font-medium text-gray-700">虚拟内存</span>
                    <span class="text-xs text-teal-600">SWAP</span>
                </div>
                <div class="text-sm font-bold text-teal-700 mb-1">${memoryData.virtual.swapTotalFormatted}</div>
                <div class="text-xs text-gray-500">
                    已用: ${memoryData.virtual.swapUsedFormatted}
                </div>
            </div>
            ` : ''}
        </div>
    `;
}
