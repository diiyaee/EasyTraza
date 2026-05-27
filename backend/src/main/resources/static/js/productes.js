let selectedId = null;

document.querySelectorAll('.btn-delete-product').forEach(btn => {
    btn.addEventListener('click', function () {
        selectedId = this.getAttribute('data-id');
        const nombre = this.getAttribute('data-nom');

        document.getElementById('modalProductText').innerText =
            `¿Seguro que quieres eliminar el producto "${nombre}"?`;
    });
});

document.getElementById('confirmDeleteProduct').addEventListener('click', function () {
    const form = document.getElementById('deleteProductForm');
    form.action = '/productes/eliminar/' + selectedId;
    form.submit();
});
