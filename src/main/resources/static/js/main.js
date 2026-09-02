/**
 * CIRT — main.js
 * Minimal JavaScript. Bootstrap handles most interactions.
 * Only add JS where it genuinely improves UX.
 */

document.addEventListener('DOMContentLoaded', function () {

    // Auto-submit the filter form when a dropdown value changes
    // so the user doesn't need to click "Apply" for dropdown-only changes.
    const filterForm = document.getElementById('filter-form');
    if (filterForm) {
        const dropdowns = filterForm.querySelectorAll('select');
        dropdowns.forEach(function (select) {
            select.addEventListener('change', function () {
                // Only auto-submit if the search field is empty
                const searchInput = filterForm.querySelector('input[name="search"]');
                if (!searchInput || searchInput.value.trim() === '') {
                    filterForm.submit();
                }
            });
        });
    }

    // Highlight the "CRITICAL" severity badge rows in tables for extra visibility
    document.querySelectorAll('.severity-critical').forEach(function (badge) {
        const row = badge.closest('tr');
        if (row) {
            row.classList.add('table-danger', 'bg-opacity-10');
        }
    });

});
