// 文件列表页面 JavaScript

// 刷新列表
function refreshList() {
    window.location.reload();
}

// 下载文件
function downloadFile(fileKey) {
    try {
        window.open(`${FileServer.baseUrl}/file/${encodeURIComponent(fileKey)}`, '_blank');
        FileServerUtils.showToast('下载已开始', 'success');
    } catch (error) {
        FileServerUtils.showToast('下载失败: ' + error.message, 'error');
    }
}

// 预览文件
function previewFile(fileKey) {
    try {
        window.open(`${FileServer.baseUrl}/file/viewFile/${encodeURIComponent(fileKey)}`, '_blank');
        FileServerUtils.showToast('预览已打开', 'success');
    } catch (error) {
        FileServerUtils.showToast('预览失败: ' + error.message, 'error');
    }
}

// 复制文件Key
function copyFileKey(fileKey) {
    FileServerUtils.copyToClipboard(fileKey);
}

// 删除文件
async function deleteFile(fileKey, fileName) {
    if (!confirm(`⚠️ 确定要删除文件 "${fileName}" 吗？\n\n此操作不可撤销！`)) {
        return;
    }

    try {
        // 显示加载提示
        FileServerUtils.showToast('正在删除文件...', 'info');

        const formData = new FormData();
        formData.append('fileKey', fileKey);

        const response = await fetch(`${FileServer.baseUrl}/file/delete`, {
            method: 'POST',
            body: formData
        });
        const data = await response.json();

        if (data.success) {
            FileServerUtils.showToast('文件删除成功', 'success');
            // 延迟刷新页面
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } else {
            FileServerUtils.showToast('删除失败: ' + data.message, 'error');
        }
    } catch (error) {
        FileServerUtils.showToast('删除失败: ' + error.message, 'error');
    }
}

// 批量操作相关功能（预留）
let selectedFiles = [];

function toggleFileSelection(fileKey) {
    const index = selectedFiles.indexOf(fileKey);
    if (index > -1) {
        selectedFiles.splice(index, 1);
    } else {
        selectedFiles.push(fileKey);
    }
    updateBatchOperationButtons();
}

function selectAllFiles() {
    const checkboxes = document.querySelectorAll('input[name="fileSelection"]');
    const selectAll = document.getElementById('selectAll').checked;
    
    selectedFiles = [];
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAll;
        if (selectAll) {
            selectedFiles.push(checkbox.value);
        }
    });
    updateBatchOperationButtons();
}

function updateBatchOperationButtons() {
    const batchButtons = document.querySelectorAll('.batch-operation');
    const hasSelection = selectedFiles.length > 0;
    
    batchButtons.forEach(button => {
        button.disabled = !hasSelection;
        button.classList.toggle('opacity-50', !hasSelection);
    });
    
    // 更新选择计数
    const countElement = document.getElementById('selectedCount');
    if (countElement) {
        countElement.textContent = selectedFiles.length;
    }
}

// 批量下载
function batchDownload() {
    if (selectedFiles.length === 0) {
        FileServerUtils.showToast('请先选择要下载的文件', 'warning');
        return;
    }
    
    selectedFiles.forEach(fileKey => {
        setTimeout(() => downloadFile(fileKey), 100);
    });
}

// 批量删除
async function batchDelete() {
    if (selectedFiles.length === 0) {
        FileServerUtils.showToast('请先选择要删除的文件', 'warning');
        return;
    }
    
    if (!confirm(`⚠️ 确定要删除选中的 ${selectedFiles.length} 个文件吗？\n\n此操作不可撤销！`)) {
        return;
    }
    
    let successCount = 0;
    let failCount = 0;
    
    FileServerUtils.showToast(`正在删除 ${selectedFiles.length} 个文件...`, 'info');
    
    for (const fileKey of selectedFiles) {
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
            } else {
                failCount++;
            }
        } catch (error) {
            failCount++;
        }
    }
    
    if (successCount > 0) {
        FileServerUtils.showToast(`成功删除 ${successCount} 个文件${failCount > 0 ? `，失败 ${failCount} 个` : ''}`, 
                                 failCount > 0 ? 'warning' : 'success');
        setTimeout(() => {
            window.location.reload();
        }, 1500);
    } else {
        FileServerUtils.showToast('删除失败', 'error');
    }
}

// 页面加载完成后的初始化
document.addEventListener('DOMContentLoaded', function() {
    // 初始化批量操作按钮状态
    updateBatchOperationButtons();
    
    // 添加键盘快捷键支持
    document.addEventListener('keydown', function(e) {
        // Ctrl+A 全选
        if (e.ctrlKey && e.key === 'a') {
            e.preventDefault();
            const selectAllCheckbox = document.getElementById('selectAll');
            if (selectAllCheckbox) {
                selectAllCheckbox.checked = !selectAllCheckbox.checked;
                selectAllFiles();
            }
        }
        
        // Delete 键删除选中文件
        if (e.key === 'Delete' && selectedFiles.length > 0) {
            e.preventDefault();
            batchDelete();
        }
        
        // Escape 键取消选择
        if (e.key === 'Escape') {
            selectedFiles = [];
            const checkboxes = document.querySelectorAll('input[name="fileSelection"]');
            checkboxes.forEach(checkbox => checkbox.checked = false);
            const selectAllCheckbox = document.getElementById('selectAll');
            if (selectAllCheckbox) selectAllCheckbox.checked = false;
            updateBatchOperationButtons();
        }
    });
});
