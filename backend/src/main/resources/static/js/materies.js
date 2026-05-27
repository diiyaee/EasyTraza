let selectedId = null;

document.addEventListener("DOMContentLoaded", function () {

    const buttons = document.querySelectorAll('.btn-delete');

    buttons.forEach(btn => {
        btn.addEventListener('click', function () {
            selectedId = this.getAttribute('data-id');
            const nombre = this.getAttribute('data-nom');

            document.getElementById('modalText').innerText =
                `¿Seguro que quieres eliminar la materia "${nombre}"?`;
        });
    });

    document.getElementById('confirmDelete').addEventListener('click', function () {
        const form = document.getElementById('deleteForm');
        form.action = '/materies/eliminar/' + selectedId;
        form.submit();
    });

});