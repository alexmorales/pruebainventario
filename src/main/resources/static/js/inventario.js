document.querySelectorAll('[data-table-filter]').forEach((input) => {
    const table = document.getElementById(input.dataset.tableFilter);
    if (!table) return;

    input.addEventListener('input', () => {
        const query = input.value.trim().toLocaleLowerCase();
        table.querySelectorAll('tbody tr[data-search]').forEach((row) => {
            row.hidden = !row.dataset.search.toLocaleLowerCase().includes(query);
        });
    });
});

document.querySelectorAll('[data-movement-type]').forEach((select) => {
    const quantityField = document.querySelector('[data-quantity-field]');
    const targetField = document.querySelector('[data-target-stock-field]');
    if (!quantityField || !targetField) return;

    const updateFields = () => {
        const isAdjustment = select.value === 'AJUSTE';
        quantityField.hidden = isAdjustment;
        targetField.hidden = !isAdjustment;
    };

    select.addEventListener('change', updateFields);
    updateFields();
});