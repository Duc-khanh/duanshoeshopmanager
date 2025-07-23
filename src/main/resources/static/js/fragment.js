
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const mobileMenuButton = document.getElementById('mobile-menu-button');

    if (mobileMenuButton) {
    mobileMenuButton.addEventListener('click', () => {
        if (sidebar.classList.contains('-translate-x-full')) {
            sidebar.classList.remove('-translate-x-full');
            overlay.classList.remove('hidden');
            mobileMenuButton.setAttribute('aria-expanded', 'true');
        } else {
            sidebar.classList.add('-translate-x-full');
            overlay.classList.add('hidden');
            mobileMenuButton.setAttribute('aria-expanded', 'false');
        }
    });
}

    overlay.addEventListener('click', () => {
    sidebar.classList.add('-translate-x-full');
    overlay.classList.add('hidden');
    mobileMenuButton.setAttribute('aria-expanded', 'false');
});

    function toggleDropdown(dropdownId) {
    const dropdown = document.getElementById(dropdownId);
    const button = dropdown.previousElementSibling;
    const isHidden = dropdown.classList.contains('hidden');
    if (isHidden) {
    dropdown.classList.remove('hidden');
    button.setAttribute('aria-expanded', 'true');
} else {
    dropdown.classList.add('hidden');
    button.setAttribute('aria-expanded', 'false');
}
}

    document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    const links = document.querySelectorAll('a[data-parent]');

    links.forEach(link => {
    const parentId = link.getAttribute('data-parent');
    const linkPath = link.pathname;

    if (currentPath === linkPath || currentPath.startsWith(linkPath)) {
    const parentDropdown = document.getElementById(parentId);
    if (parentDropdown) {
    parentDropdown.classList.remove('hidden');
    const button = parentDropdown.previousElementSibling;
    if (button) {
    button.setAttribute('aria-expanded', 'true');
}
}

    link.classList.add('text-blue-600', 'font-bold');
}
});
});

