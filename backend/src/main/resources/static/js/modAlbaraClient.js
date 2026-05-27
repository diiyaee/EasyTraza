let liniaIndex = document.querySelectorAll("#liniesContainer > div").length;

const container = document.getElementById("liniesContainer");
const btnAdd = document.getElementById("addLiniaBtn");

// ➕ añadir línea
btnAdd.addEventListener("click", () => {

    const div = document.createElement("div");

    div.classList.add("border", "p-3", "mb-3");

    div.innerHTML = `

        <label>Producto</label>

        <select name="linies[${liniaIndex}].producte.id"
                class="form-control">

            ${document.getElementById("producteOptions").innerHTML}

        </select>

        <label class="mt-2">Cantidad</label>

        <input type="number"
               step="0.01"
               name="linies[${liniaIndex}].quantitat"
               class="form-control">

        <button type="button"
                class="btn btn-danger btn-sm mt-2 removeLinia">

            Eliminar
        </button>
    `;

    div.querySelector(".removeLinia").addEventListener("click", () => {

        div.remove();

        reindex();
    });

    container.appendChild(div);

    liniaIndex++;
});

// 🔁 reindex
function reindex() {

    const linies = document.querySelectorAll("#liniesContainer > div");

    linies.forEach((l, index) => {

        l.querySelectorAll("input").forEach(input => {

            if (input.name) {

                input.name = input.name.replace(
                    /linies\[\d+\]/,
                    `linies[${index}]`
                );
            }
        });

        l.querySelectorAll("select").forEach(select => {

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

// 🗑 eliminar existentes
document.querySelectorAll(".removeLinia").forEach(btn => {

    btn.addEventListener("click", () => {

        btn.closest("div").remove();

        reindex();
    });
});

// 🚨 validación mínima
document.getElementById("albaraForm").addEventListener("submit", (e) => {

    const linies = document.querySelectorAll("#liniesContainer > div");

    if (linies.length === 0) {

        e.preventDefault();

        alert("Debes añadir al menos una línea");
    }
});