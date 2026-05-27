document.addEventListener("DOMContentLoaded", function() {
    
    const deleteModal = document.getElementById('deleteAlbaraModal');
    const deleteForm = document.getElementById('deleteAlbaraForm');
    const confirmBtn = document.getElementById('confirmDeleteAlbara');

    // 1. Cuando el modal se va a abrir...
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function(event) {
            // El botón de la tabla que disparó el evento
            const button = event.relatedTarget;
            
            // Leemos los datos inyectados por Thymeleaf
            const idAlbaran = button.getAttribute('data-id');
            const numAlbaran = button.getAttribute('data-num');

            // Actualizamos el mensaje visual para que quede más profesional
            document.getElementById('modalAlbaraText').textContent = '¿Seguro que quieres eliminar el albarán ' + numAlbaran + '?';

            // Actualizamos la acción del formulario con el ID real
            deleteForm.action = '/albarans-proveidor/eliminar/' + idAlbaran;
        });
    }

    // 2. Cuando el usuario pulsa el botón rojo de "Eliminar" en el modal...
    if (confirmBtn) {
        confirmBtn.addEventListener('click', function() {
            deleteForm.submit(); // Disparamos la petición POST al servidor
        });
    }
});