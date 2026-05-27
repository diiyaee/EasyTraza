let liniaIndex =
    document.querySelectorAll("#liniesContainer > div").length;

const container = document.getElementById("liniesContainer");
const btnAdd = document.getElementById("addLiniaBtn");


// ================================
// OCR LOADING
// ================================

const ocrForm = document.getElementById("ocrForm");

if (ocrForm) {

    ocrForm.addEventListener("submit", () => {

        document.getElementById("loadingOCR")
            .style.display = "block";
    });
}


// ================================
// AÑADIR LÍNEA
// ================================

btnAdd.addEventListener("click", () => {

    const div = document.createElement("div");

    div.classList.add(
        "border",
        "rounded",
        "p-3",
        "mb-3",
        "bg-light"
    );

    div.innerHTML = `

        <label>Número lote</label>

        <input type="text"
               name="linies[${liniaIndex}].lot.numLot"
               class="form-control">

        <label class="mt-2">
            Fecha caducidad
        </label>

        <input type="date"
               name="linies[${liniaIndex}].lot.dataCaducitat"
               class="form-control"
               min="${new Date().toISOString().split('T')[0]}">

        <label class="mt-2">
            Materia
        </label>

        <select name="linies[${liniaIndex}].lot.materia.id"
                class="form-control">

            ${document.getElementById("materiaOptions").innerHTML}

        </select>

        <label class="mt-2">
            Cantidad
        </label>

        <input type="number"
               step="0.01"
               name="linies[${liniaIndex}].quantitat"
               class="form-control">

        <label class="mt-2">
            Unidad
        </label>

        <input type="text"
               name="linies[${liniaIndex}].unitats"
               class="form-control"
               placeholder="kg, L, cajas...">

        <button type="button"
                class="btn btn-danger btn-sm mt-3 removeLinia">

            Eliminar

        </button>
    `;

    div.querySelector(".removeLinia")
        .addEventListener("click", () => {

            div.remove();

            reindex();
        });

    container.appendChild(div);

    liniaIndex++;
});


// ================================
// REINDEX
// ================================

function reindex() {

    const linies =
        document.querySelectorAll("#liniesContainer > div");

    linies.forEach((linia, index) => {

        linia.querySelectorAll("input")
            .forEach(input => {

                if (input.name) {

                    input.name = input.name.replace(
                        /linies\[\d+\]/,
                        `linies[${index}]`
                    );
                }
            });

        linia.querySelectorAll("select")
            .forEach(select => {

                if (select.name) {

                    select.name = select.name.replace(
                        /linies\[\d+\]/,
                        `linies[${index}]`
                    );
                }
            });
    });

    liniaIndex = linies.length;
}


// ================================
// ELIMINAR EXISTENTES
// ================================

document.querySelectorAll(".removeLinia")
    .forEach(btn => {

        btn.addEventListener("click", () => {

            btn.closest(".border").remove();

            reindex();
        });
    });


// ================================
// VALIDACIÓN
// ================================

document.getElementById("albaraForm")
    .addEventListener("submit", (e) => {

        const linies =
            document.querySelectorAll("#liniesContainer > div");

        if (linies.length === 0) {

            e.preventDefault();

            alert("Debes añadir al menos una línea");
        }
    });