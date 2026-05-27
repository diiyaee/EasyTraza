let selectedClientId = null;

document.addEventListener("DOMContentLoaded", function () {

    const buttons = document.querySelectorAll('.btn-delete-client');

    buttons.forEach(btn => {
        btn.addEventListener('click', function () {

            selectedClientId = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nom');

            document.getElementById('modalClientText').innerText =
                `¿Seguro que quieres eliminar el cliente "${nombre}"?`;
        });
    });

    document.getElementById('confirmDeleteClient').addEventListener('click', function () {

        const form = document.getElementById('deleteClientForm');
        form.action = '/clients/eliminar/' + selectedClientId;
        form.submit();
    });

});