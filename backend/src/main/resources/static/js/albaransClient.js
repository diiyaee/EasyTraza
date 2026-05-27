let selectedId = null;

document.querySelectorAll('.btn-delete-albara').forEach(btn => {

    btn.addEventListener('click', function () {

        selectedId = this.getAttribute('data-id');

        const client = this.getAttribute('data-client');

        document.getElementById('modalAlbaraText').innerText =
            `¿Seguro que quieres eliminar el albarán del cliente "${client}"?`;
    });
});

document.getElementById('confirmDeleteAlbara').addEventListener('click', function () {

    const form = document.getElementById('deleteAlbaraForm');

    form.action = '/albarans-client/eliminar/' + selectedId;

    form.submit();
});