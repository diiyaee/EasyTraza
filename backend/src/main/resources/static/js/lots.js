document.addEventListener("DOMContentLoaded", function () {

    const params = new URLSearchParams(window.location.search);

    const hasConflict = params.get("conflict");
    const lotId = params.get("lotId");

    if (hasConflict === "true") {

        const modal = new bootstrap.Modal(document.getElementById('conflictModal'));
        modal.show();

        const form = document.getElementById("forceForm");

        form.action = "/lots/canviar-estat/" + lotId;
    }
});
