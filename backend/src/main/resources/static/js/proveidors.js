let selectedProviderId = null;

document.addEventListener("DOMContentLoaded", function () {

    const buttons = document.querySelectorAll('.btn-delete-provider');

    buttons.forEach(btn => {
        btn.addEventListener('click', function () {
            selectedProviderId = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nom');

            document.getElementById('modalProviderText').innerText =
                `¿Seguro que quieres eliminar el proveedor "${nombre}"?`;
        });
    });

    document.getElementById('confirmDeleteProvider').addEventListener('click', function () {
        const form = document.getElementById('deleteProviderForm');
        form.action = '/proveidors/eliminar/' + selectedProviderId;
        form.submit();
    });

});
