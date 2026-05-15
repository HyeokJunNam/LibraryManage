document.addEventListener("DOMContentLoaded", function () {
    const navGroups = document.querySelectorAll(".admin-nav__group");

    navGroups.forEach(function (group) {
        const toggle = group.querySelector(".admin-nav__toggle");
        const subMenu = group.querySelector(".admin-nav__sub");

        if (!toggle || !subMenu) {
            return;
        }

        const syncExpanded = function () {
            const isOpen = group.classList.contains("admin-nav__group--open");
            toggle.setAttribute("aria-expanded", String(isOpen));
        };

        syncExpanded();

        toggle.addEventListener("click", function () {
            group.classList.toggle("admin-nav__group--open");
            syncExpanded();
        });
    });
});