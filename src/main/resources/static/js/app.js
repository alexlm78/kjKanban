// Global application utilities and helpers

// API Base URL
const API_BASE = '/api';

// Notification system
function showNotification(message, type = 'info', duration = 3000) {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    // Add to DOM
    document.body.appendChild(notification);

    // Auto remove after duration
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, duration);
}

// API Helper functions
const api = {
    async get(url) {
        try {
            const response = await fetch(`${API_BASE}${url}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('API GET error:', error);
            throw error;
        }
    },

    async post(url, data) {
        try {
            const response = await fetch(`${API_BASE}${url}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.error || `HTTP error! status: ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error('API POST error:', error);
            throw error;
        }
    },

    async put(url, data) {
        try {
            const response = await fetch(`${API_BASE}${url}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.error || `HTTP error! status: ${response.status}`);
            }
            
            return result;
        } catch (error) {
            console.error('API PUT error:', error);
            throw error;
        }
    },

    async delete(url) {
        try {
            const response = await fetch(`${API_BASE}${url}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                const result = await response.json();
                throw new Error(result.error || `HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API DELETE error:', error);
            throw error;
        }
    }
};

// Date formatting utilities
const dateUtils = {
    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    },

    formatDateTime(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    isOverdue(dateString) {
        if (!dateString) return false;
        const dueDate = new Date(dateString);
        const now = new Date();
        return dueDate < now;
    },

    isDueSoon(dateString, hoursThreshold = 24) {
        if (!dateString) return false;
        const dueDate = new Date(dateString);
        const now = new Date();
        const diffHours = (dueDate - now) / (1000 * 60 * 60);
        return diffHours > 0 && diffHours <= hoursThreshold;
    },

    toInputFormat(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toISOString().slice(0, 16);
    }
};

// Form validation utilities
const validation = {
    validateRequired(value, fieldName) {
        if (!value || value.trim() === '') {
            throw new Error(`${fieldName} is required`);
        }
        return true;
    },

    validateMaxLength(value, maxLength, fieldName) {
        if (value && value.length > maxLength) {
            throw new Error(`${fieldName} must not exceed ${maxLength} characters`);
        }
        return true;
    },

    validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (email && !emailRegex.test(email)) {
            throw new Error('Please enter a valid email address');
        }
        return true;
    },

    validateColor(color) {
        const colorRegex = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/;
        if (color && !colorRegex.test(color)) {
            throw new Error('Please enter a valid hex color');
        }
        return true;
    }
};

// DOM utilities
const dom = {
    createElement(tag, className = '', textContent = '') {
        const element = document.createElement(tag);
        if (className) element.className = className;
        if (textContent) element.textContent = textContent;
        return element;
    },

    removeAllChildren(element) {
        while (element.firstChild) {
            element.removeChild(element.firstChild);
        }
    },

    show(element) {
        element.style.display = 'block';
    },

    hide(element) {
        element.style.display = 'none';
    },

    toggle(element) {
        element.style.display = element.style.display === 'none' ? 'block' : 'none';
    }
};

// Loading state management
const loading = {
    show(element, message = 'Loading...') {
        element.innerHTML = `
            <div class="loading">
                <i class="fas fa-spinner"></i>
                <span>${message}</span>
            </div>
        `;
    },

    hide(element) {
        const loadingElement = element.querySelector('.loading');
        if (loadingElement) {
            loadingElement.remove();
        }
    }
};

// Local storage utilities
const storage = {
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('Error saving to localStorage:', error);
        }
    },

    get(key, defaultValue = null) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (error) {
            console.error('Error reading from localStorage:', error);
            return defaultValue;
        }
    },

    remove(key) {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error('Error removing from localStorage:', error);
        }
    },

    clear() {
        try {
            localStorage.clear();
        } catch (error) {
            console.error('Error clearing localStorage:', error);
        }
    }
};

// Debounce utility for search and input handling
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Throttle utility for scroll and resize events
function throttle(func, limit) {
    let inThrottle;
    return function() {
        const args = arguments;
        const context = this;
        if (!inThrottle) {
            func.apply(context, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// Initialize common functionality
document.addEventListener('DOMContentLoaded', function() {
    // Close modals when clicking outside
    document.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal')) {
            event.target.style.display = 'none';
        }
    });

    // Close modals with Escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            const openModals = document.querySelectorAll('.modal[style*="block"], .modal[style*="flex"]');
            openModals.forEach(modal => {
                modal.style.display = 'none';
            });
        }
    });

    // Auto-resize textareas
    document.addEventListener('input', function(event) {
        if (event.target.tagName === 'TEXTAREA') {
            event.target.style.height = 'auto';
            event.target.style.height = event.target.scrollHeight + 'px';
        }
    });

    // Color preset functionality
    document.addEventListener('click', function(event) {
        if (event.target.classList.contains('color-preset')) {
            const color = event.target.getAttribute('data-color');
            const colorInput = event.target.closest('.color-picker').querySelector('input[type="color"]');
            if (colorInput) {
                colorInput.value = color;
            }
        }
    });
});

// Export utilities for use in other scripts
window.app = {
    api,
    dateUtils,
    validation,
    dom,
    loading,
    storage,
    showNotification,
    debounce,
    throttle
};