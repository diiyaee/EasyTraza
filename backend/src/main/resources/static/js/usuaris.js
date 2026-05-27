let selectedUserId = null;

document.addEventListener("DOMContentLoaded", function () {

    const buttons = document.querySelectorAll('.btn-delete-user');

    buttons.forEach(btn => {
        btn.addEventListener('click', function () {
            selectedUserId = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nom');

            document.getElementById('modalUserText').innerText =
                `¿Seguro que quieres eliminar al usuario "${nombre}"?`;
        });
    });

    document.getElementById('confirmDeleteUser').addEventListener('click', function () {
        const form = document.getElementById('deleteUserForm');
        form.action = '/usuaris/eliminar/' + selectedUserId;
        form.submit();
    });

});