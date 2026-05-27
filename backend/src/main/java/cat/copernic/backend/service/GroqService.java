/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author diyae
 */
@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.groq.com/openai/v1")
            .build();

    public String interpretarText(String text) {

        String prompt = """
        Eres un sistema profesional de extracción estructurada de datos de albaranes.

        Tu única tarea es devolver JSON VÁLIDO.
        NO expliques nada.
        NO uses markdown.
        NO escribas frases.
        NO uses ```json.
        NO añadas comentarios.
        NO inventes información.

        Si un dato no puede determinarse con seguridad:
        usa null.

        ==================================================
        FORMATO JSON OBLIGATORIO
        ==================================================

        {
          "numAlbara": "",
          "proveidor": "",
          "dataRecepcio": "YYYY-MM-DD",
          "linies": [
            {
              "numLot": "",
              "materia": "",
              "quantitat": 0,
              "unitats": "",
              "dataCaducitat": "YYYY-MM-DD"
            }
          ]
        }

        ==================================================
        REGLAS DE FECHAS
        ==================================================

        TODAS las fechas deben devolverse SIEMPRE en formato:

        YYYY-MM-DD

        Convierte automáticamente:
        - DD/MM/YYYY
        - DD-MM-YYYY
        - DD.MM.YYYY

        Ejemplo:
        22/04/2026
        → 2026-04-22

        Si no puedes convertirla:
        → null

        ==================================================
        REGLA CRÍTICA:
        SEPARACIÓN ENTRE "materia" Y "unitats"
        ==================================================

        Debes DIVIDIR correctamente el nombre del producto.

        --------------------------------------------------
        A) "materia"
        --------------------------------------------------

        "materia" debe contener SOLAMENTE:
        - nombre comercial
        - descripción textual limpia del producto

        "materia" NO puede contener:
        - formatos
        - tamaños
        - litros
        - kilos
        - gramos
        - packs
        - cajas
        - multiplicadores
        - códigos
        - medidas
        - unidades
        - referencias de embalaje
        - números de formato

        ELIMINA de "materia" cualquier token como:

        KG
        KGS
        G
        GR
        GRAM
        L
        LT
        LTR
        LITRE
        LITRES
        ML
        CL
        X
        x
        C/
        S/
        P/KG
        PACK
        BOX
        BOXES
        CAJA
        CAJAS
        BRICK
        REX
        BOLSA
        SACO
        SACO25
        SACO50
        12X1LT
        6X2LT
        3X1KG
        2X5KG
        1LT
        5KG
        25KG
        10KG
        500GR
        750ML

        También elimina:
        - combinaciones número + unidad
        - multiplicadores
        - formatos industriales

        --------------------------------------------------
        B) "unitats"
        --------------------------------------------------

        "unitats" debe contener TODO lo eliminado de "materia".

        IMPORTANTE:
        NO pierdas información.

        Si un producto contiene:
        - formatos
        - litros
        - kilos
        - packs
        - multiplicadores
        - medidas
        - códigos de embalaje

        TODO eso va en "unitats".

        --------------------------------------------------
        EJEMPLOS OBLIGATORIOS
        --------------------------------------------------

        Entrada:
        "NATA PRESIDENT 35% REX2 LT (LITRE)"

        Resultado:
        materia = "NATA PRESIDENT 35%"
        unitats = "REX2 LT (LITRE)"

        --------------------------------------------------

        Entrada:
        "LLET SENCERA BRICK 1LT. PRESIDENT 12X1LT"

        Resultado:
        materia = "LLET SENCERA PRESIDENT"
        unitats = "BRICK 1LT 12X1LT"

        --------------------------------------------------

        Entrada:
        "BRISA FARINA S/25 KG."

        Resultado:
        materia = "BRISA FARINA"
        unitats = "S/25 KG"

        --------------------------------------------------

        Entrada:
        "TRAMEZZINI PANE SPUNTINELLE C/4U.X1KG"

        Resultado:
        materia = "TRAMEZZINI PANE SPUNTINELLE"
        unitats = "C/4U.X1KG"

        ==================================================
        REGLAS DE CANTIDAD (MUY IMPORTANTE)
        ==================================================

        ANTES de extraer cantidades:

        1. Detecta las cabeceras reales de la tabla.
        2. Detecta cuál es la columna de cantidad ENTREGADA.
        3. Usa SOLO esa columna.

        Posibles nombres válidos de columna de cantidad:

        QUANT
        QUANTITAT
        CANTIDAD
        QTY
        UDS
        UNIDADES
        UNITATS
        ENTREGADO
        SACOS
        CAJAS
        BOXES
        KG
        KGS
        TONELADAS
        PESO

        ==================================================
        PROHIBIDO USAR COMO "quantitat"
        ==================================================

        NUNCA uses columnas:
        - PREU
        - PRECIO
        - DTE
        - DTO
        - DESCUENTO
        - IMPORT
        - TOTAL
        - IVA
        - SUBTOTAL
        - BASE
        - IMPORTE

        ==================================================
        REGLAS IMPORTANTES DE CANTIDAD
        ==================================================

        Si hay varias columnas numéricas:
        usa SOLO la asociada a la cabecera de cantidad.

        NO uses:
        - precios
        - importes
        - descuentos
        - toneladas
        - formatos de unidad

        --------------------------------------------------
        EJEMPLO 1
        --------------------------------------------------

        Cabecera:
        DESCRIPCIO | LOT | QUANT | PREU | DTE | IMPORT

        Fila:
        NATA PRESIDENT ... 24,00 5,56 15 113,42

        Resultado:
        quantitat = 24.00

        NO usar:
        5.56
        15
        113.42

        --------------------------------------------------
        EJEMPLO 2
        --------------------------------------------------

        Cabecera:
        ARTICULO | CONCEPTO | F. CONSUMO PREFERENTE | LOTE | UDS. | SACOS | TONELADAS

        Fila:
        02133 | HARINA PANIF. ILERDA Esp. | 14/10/2026 | M1983086 | SACO25 | 41 | 1,025

        Resultado:
        quantitat = 41

        NO usar:
        SACO25
        1,025

        porque "SACOS" es la columna de cantidad real.

        ==================================================
        REGLAS DE LOTES (CRÍTICO)
        ==================================================

        "numLot" SOLO puede contener:
        - el valor situado bajo la columna:
          LOT
          LOTE
          NUM LOT
          Nº LOTE
          BATCH

        NO usar jamás:
        - CODI
        - CODIGO
        - REFERENCIA
        - REF
        - ARTICULO
        - SKU
        - ID PRODUCTO

        NO confundas:
        - códigos de producto
        - referencias internas
        - códigos comerciales

        con el lote.

        --------------------------------------------------
        EJEMPLO CORRECTO
        --------------------------------------------------

        Cabecera:
        CODI | DESCRIPCIO | LOT | QUANT

        Fila:
        LCT116538 | NATA PRESIDENT... | 2609032 | 24,00

        Resultado:
        numLot = "2609032"

        NO usar:
        "LCT116538"

        --------------------------------------------------
        EJEMPLO CORRECTO 2
        --------------------------------------------------

        Cabecera:
        ARTICULO | CONCEPTO | LOTE | SACOS

        Fila:
        02133 | HARINA PANIF. | M1983086 | 41

        Resultado:
        numLot = "M1983086"

        NO usar:
        "02133"

        ==================================================
        LIMPIEZA DE TEXTO
        ==================================================

        Ignora:
        ? ¿ ! ¡ * # =

        Limpia:
        - espacios duplicados
        - saltos raros
        - caracteres OCR corruptos

        ==================================================
        REGLA FINAL ABSOLUTA
        ==================================================

        Devuelve EXCLUSIVAMENTE JSON válido.

        Nada más.

        TEXTO:
        """ + text;

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0
        );

        return webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .bodyValue(body)
            .retrieve()

            // 🔥 CAPTURA REAL DEL ERROR DE GROQ
            .onStatus(
                    status -> status.isError(),
                    response -> response.bodyToMono(String.class)
                            .map(errorBody -> new RuntimeException(
                                    "❌ GROQ ERROR: " + response.statusCode() + "\n" + errorBody
                            ))
            )

            // 🔥 RESPUESTA OK
            .bodyToMono(JsonNode.class)
            .map(json ->
                    json.get("choices")
                            .get(0)
                            .get("message")
                            .get("content")
                            .asText()
            )
            .block();
    }
}