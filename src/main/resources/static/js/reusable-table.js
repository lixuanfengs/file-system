/**
 * 通用动态表格组件
 * 支持批量操作、列显示设置、数据操作等功能
 */
class DynamicTable {
    constructor(config) {
        this.config = {
            tableId: '',
            actionBaseUrl: '/file',
            enableBatch: true,
            enableColumnSettings: true,
            ...config
        };

        this.selectedItems = [];
        this.elements = {};

        this.init();
    }

    /**
     * 初始化表格
     */
    init() {
        this.initElements();
        this.bindEvents();
        this.loadColumnSettings();
        this.updateBatchActions();
    }

    /**
     * 初始化DOM元素
     */
    initElements() {
        const { tableId } = this.config;

        this.elements = {
            container: document.getElementById(`${tableId}-container`),
            selectAllCheckbox: document.getElementById(`${tableId}-selectAll`),
            batchActions: document.getElementById(`${tableId}-batchActions`),
            selectedCount: document.getElementById(`${tableId}-selectedCount`),
            batchDownloadBtn: document.getElementById(`${tableId}-batchDownloadBtn`),
            batchDeleteBtn: document.getElementById(`${tableId}-batchDeleteBtn`),
            columnModal: document.getElementById(`${tableId}-columnModal`),
            columnToggleButton: document.getElementById(`${tableId}-columnToggleBtn`),
            applySettingsButton: document.getElementById(`${tableId}-applySettingsBtn`),
            resetSettingsButton: document.getElementById(`${tableId}-resetSettingsBtn`),
        };

        if (!this.elements.container) {
            console.warn(`Table container not found: ${tableId}-container`);
            return;
        }

        // 获取动态元素
        this.elements.itemCheckboxes = this.elements.container.querySelectorAll('.file-item-checkbox');
        this.elements.deleteButtons = this.elements.container.querySelectorAll(`[data-action='delete-file-${tableId}']`);
        this.elements.previewButtons = this.elements.container.querySelectorAll(`[data-action='preview-file-${tableId}']`);

        // 预览相关元素（已移除iframe预览功能）

        if (this.elements.columnModal) {
            this.elements.columnCheckboxes = this.elements.columnModal.querySelectorAll('input[type="checkbox"]');
            this.elements.closeModalButtons = this.elements.columnModal.querySelectorAll(`[data-action='close-modal-${tableId}']`);
        }
    }


    /**
     * 绑定事件监听器
     */
    bindEvents() {
        const { elements } = this;

        // 全选/取消全选
        if (elements.selectAllCheckbox) {
            elements.selectAllCheckbox.addEventListener('change', () => this.toggleSelectAll());
        }

        // 单项选择
        elements.itemCheckboxes.forEach(cb => {
            cb.addEventListener('change', () => this.updateBatchActions());
        });

        // 列设置相关
        if (elements.columnToggleButton) {
            elements.columnToggleButton.addEventListener('click', () => this.toggleColumnModal());
        }

        if (elements.closeModalButtons) {
            elements.closeModalButtons.forEach(btn => {
                btn.addEventListener('click', () => this.toggleColumnModal());
            });
        }

        if (elements.columnModal) {
            elements.columnModal.addEventListener('click', (e) => {
                if (e.target === elements.columnModal) {
                    this.toggleColumnModal();
                }
            });
        }

        if (elements.applySettingsButton) {
            elements.applySettingsButton.addEventListener('click', () => {
                this.saveColumnSettings();
                this.toggleColumnModal();
                this.showToast('显示设置已应用', 'success');
            });
        }

        if (elements.resetSettingsButton) {
            elements.resetSettingsButton.addEventListener('click', () => {
                this.resetColumnSettings();
            });
        }

        if (elements.columnCheckboxes) {
            elements.columnCheckboxes.forEach(cb => {
                cb.addEventListener('change', () => {
                    this.applyColumnVisibility(cb.dataset.column, cb.checked);
                });
            });
        }

        // 删除按钮
        elements.deleteButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const { uuid, name } = e.currentTarget.dataset;
                this.deleteFile(uuid, name);
            });
        });

        // 预览按钮
        elements.previewButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const { uuid, name } = e.currentTarget.dataset;
                this.previewFile(uuid, name);
            });
        });

        // 预览模态框相关功能已移除（改为新标签页打开）

        // 批量操作
        if (elements.batchDownloadBtn) {
            elements.batchDownloadBtn.addEventListener('click', () => this.batchDownload());
        }

        if (elements.batchDeleteBtn) {
            elements.batchDeleteBtn.addEventListener('click', () => this.batchDelete());
        }

        // ESC键关闭模态框
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                if (elements.columnModal && !elements.columnModal.classList.contains('hidden')) {
                    this.toggleColumnModal();
                }
            }
        });
    }

    /**
     * 获取基础URL
     */
    getBaseUrl() {
        return typeof FileServer !== 'undefined' ? FileServer.baseUrl : window.location.origin;
    }

    /**
     * 显示提示消息
     */
    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 transform translate-x-full`;

        const typeClasses = {
            success: 'bg-green-500 text-white',
            error: 'bg-red-500 text-white',
            warning: 'bg-yellow-500 text-white',
            info: 'bg-blue-500 text-white'
        };

        toast.className += ` ${typeClasses[type] || typeClasses.info}`;
        toast.textContent = message;
        document.body.appendChild(toast);

        setTimeout(() => toast.classList.remove('translate-x-full'), 100);
        setTimeout(() => {
            toast.classList.add('translate-x-full');
            setTimeout(() => {
                if (toast.parentElement) {
                    toast.parentElement.removeChild(toast);
                }
            }, 300);
        }, 3000);
    }

    /**
     * 应用列显示/隐藏
     */
    applyColumnVisibility(columnName, isVisible) {
        this.elements.container.querySelectorAll(`.col-${columnName}`).forEach(el => {
            el.style.display = isVisible ? '' : 'none';
        });
    }

    /**
     * 保存列设置
     */
    saveColumnSettings() {
        const settings = {};
        this.elements.columnCheckboxes.forEach(cb => {
            settings[cb.dataset.column] = cb.checked;
        });
        localStorage.setItem(`${this.config.tableId}-columns`, JSON.stringify(settings));
    }

    /**
     * 加载列设置
     */
    loadColumnSettings() {
        if (!this.elements.columnCheckboxes) return;

        const saved = JSON.parse(localStorage.getItem(`${this.config.tableId}-columns`) || '{}');
        this.elements.columnCheckboxes.forEach(cb => {
            const columnName = cb.dataset.column;
            cb.checked = saved[columnName] === undefined ? cb.hasAttribute('checked') : saved[columnName];
            this.applyColumnVisibility(columnName, cb.checked);
        });
    }

    /**
     * 重置列设置
     */
    resetColumnSettings() {
        localStorage.removeItem(`${this.config.tableId}-columns`);
        if (this.elements.columnCheckboxes) {
            this.elements.columnCheckboxes.forEach(cb => {
                cb.checked = cb.hasAttribute('checked');
                this.applyColumnVisibility(cb.dataset.column, cb.checked);
            });
        }
        this.showToast('已重置为默认设置', 'success');
    }

    /**
     * 切换列设置模态框
     */
    toggleColumnModal() {
        if (!this.elements.columnModal) return;

        this.elements.columnModal.classList.toggle('hidden');
        document.body.style.overflow = this.elements.columnModal.classList.contains('hidden') ? '' : 'hidden';
    }

    /**
     * 更新批量操作状态
     */
    updateBatchActions() {
        this.selectedItems = Array.from(this.elements.itemCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.value);

        if (this.elements.selectedCount) {
            this.elements.selectedCount.textContent = this.selectedItems.length;
        }

        if (this.elements.batchActions) {
            this.elements.batchActions.style.display = this.selectedItems.length > 0 ? 'flex' : 'none';
        }

        // 更新全选状态
        if (this.elements.selectAllCheckbox) {
            const totalItems = this.elements.itemCheckboxes.length;
            const selectedCount = this.selectedItems.length;

            if (selectedCount === 0) {
                this.elements.selectAllCheckbox.checked = false;
                this.elements.selectAllCheckbox.indeterminate = false;
            } else if (selectedCount === totalItems && totalItems > 0) {
                this.elements.selectAllCheckbox.checked = true;
                this.elements.selectAllCheckbox.indeterminate = false;
            } else {
                this.elements.selectAllCheckbox.checked = false;
                this.elements.selectAllCheckbox.indeterminate = true;
            }
        }
    }

    /**
     * 切换全选状态
     */
    toggleSelectAll() {
        if (!this.elements.selectAllCheckbox) return;

        const isChecked = this.elements.selectAllCheckbox.checked;
        this.elements.itemCheckboxes.forEach(cb => {
            cb.checked = isChecked;
        });

        this.updateBatchActions();
    }


    /**
     * 删除单个文件
     */
    async deleteFile(uuid, fileName) {
        if (!confirm(`确定要删除文件 "${fileName}" 吗？`)) {
            return;
        }

        const formData = new FormData();
        formData.append('fileKey', uuid);

        try {
            const response = await fetch(`${this.getBaseUrl()}${this.config.actionBaseUrl}/delete`, {
                method: 'POST',
                body: formData
            });

            const data = await response.json();

            if (data.success) {
                this.showToast('文件删除成功', 'success');
                setTimeout(() => window.location.reload(), 1000);
            } else {
                this.showToast(`删除失败: ${data.message}`, 'error');
            }
        } catch (error) {
            this.showToast(`删除失败: ${error.message}`, 'error');
        }
    }

    /**
     * 批量删除文件
     */
    async batchDelete() {
        if (this.selectedItems.length === 0) {
            this.showToast('请先选择要删除的文件', 'warning');
            return;
        }

        if (!confirm(`确定要删除选中的 ${this.selectedItems.length} 个文件吗？`)) {
            return;
        }

        const deleteBtn = this.elements.batchDeleteBtn;
        deleteBtn.disabled = true;
        deleteBtn.textContent = '删除中...';

        let successCount = 0;
        let failCount = 0;

        for (const fileKey of this.selectedItems) {
            const formData = new FormData();
            formData.append('fileKey', fileKey);

            try {
                const response = await fetch(`${this.getBaseUrl()}${this.config.actionBaseUrl}/delete`, {
                    method: 'POST',
                    body: formData
                });

                const result = await response.json();
                if (result.success) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (error) {
                failCount++;
                console.error('Delete error:', error);
            }
        }

        const message = `删除完成: ${successCount} 成功, ${failCount} 失败`;
        this.showToast(message, failCount > 0 ? 'warning' : 'success');

        if (successCount > 0) {
            setTimeout(() => window.location.reload(), 1500);
        } else {
            deleteBtn.disabled = false;
            deleteBtn.textContent = '批量删除';
        }
    }

    /**
     * 批量下载文件
     */
    batchDownload() {
        if (this.selectedItems.length === 0) {
            this.showToast('请先选择要下载的文件', 'warning');
            return;
        }

        this.showToast(`开始下载 ${this.selectedItems.length} 个文件...`, 'info');

        this.selectedItems.forEach((fileKey, index) => {
            setTimeout(() => {
                const url = `${this.getBaseUrl()}${this.config.actionBaseUrl}/${encodeURIComponent(fileKey)}`;
                window.open(url, '_blank');
            }, index * 500); // 延迟下载，避免浏览器阻止多个下载
        });
    }

    /**
     * 预览文件 - 在新标签页中打开
     */
    async previewFile(uuid, fileName) {
        try {
            // 构建预览URL
            const previewUrl = `${this.getBaseUrl()}/file/preview/${encodeURIComponent(uuid)}`;

            // 调试信息
            console.log('在新标签页预览文件:', {
                uuid: uuid,
                fileName: fileName,
                previewUrl: previewUrl
            });

            // 在新标签页中打开预览
            const newWindow = window.open(previewUrl, '_blank');

            if (!newWindow) {
                this.showToast('无法打开新标签页，请检查浏览器弹窗设置', 'error');
                return;
            }

            // 显示成功提示
            this.showToast(`正在新标签页中预览: ${fileName}`, 'success');

        } catch (error) {
            console.error('Preview error:', error);
            this.showToast('预览失败: ' + error.message, 'error');
        }
    }






}

/**
 * 表格管理器 - 管理页面中的所有表格实例
 */
class TableManager {
    constructor() {
        this.tables = new Map();
    }

    /**
     * 注册表格实例
     */
    register(tableId, config) {
        const table = new DynamicTable({ tableId, ...config });
        this.tables.set(tableId, table);
        return table;
    }

    /**
     * 获取表格实例
     */
    get(tableId) {
        return this.tables.get(tableId);
    }

    /**
     * 自动初始化页面中的表格
     */
    autoInit() {
        document.addEventListener('DOMContentLoaded', () => {
            // 查找所有表格容器
            const containers = document.querySelectorAll('.dynamic-table-container');

            containers.forEach(container => {
                const tableId = container.id.replace('-container', '');
                const actionBaseUrl = container.dataset.actionBaseUrl || '/file';

                this.register(tableId, {
                    actionBaseUrl: actionBaseUrl
                });
            });
        });
    }
}

// 创建全局表格管理器实例
window.TableManager = new TableManager();

// 自动初始化
window.TableManager.autoInit();

// 兼容旧版本的初始化函数
function initializeReusableTable(config) {
    return new DynamicTable(config);
}

