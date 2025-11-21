import json
import re

json_file = "icons.json"
output_file = "FontAwesome7FreeEnum.java"

with open(json_file, "r", encoding="utf-8") as f:
    icons = json.load(f)

enum_entries = []

for key, icon in icons.items():
    # Nur Free Icons berücksichtigen
    if "free" not in icon or "solid" not in icon["free"]:
        continue

    label = icon["label"]
    style = "SOLID"  # Free = solid in deiner json

    # Zahlen am Anfang zu N_0, N_1 …
    if label[0].isdigit():
        label = f"N_{label}"

    # Enum Name: LABEL_STYLE
    enum_name = label.upper()
    # Leerzeichen und Bindestriche zu '_', andere Sonderzeichen entfernen
    enum_name = re.sub(r'[\s\-]+', '_', enum_name)
    enum_name = re.sub(r'[^A-Z0-9_]', '', enum_name)
    enum_name = f"{enum_name}_{style}"

    # Unicode in Java Format: \uXXXX
    unicode_hex = int(icon["unicode"], 16)
    unicode_java = f"\\u{unicode_hex:04X}"

    enum_entries.append((enum_name, unicode_java))

# Alphabetisch sortieren
enum_entries.sort(key=lambda x: x[0])

# Java Enum generieren
with open(output_file, "w", encoding="utf-8") as f:
    f.write("public enum FontAwesome7Free {\n\n")

    for name, code in enum_entries:
        f.write(f"    {name}(\"{code}\"),\n")

    f.write("    ;\n\n")
    f.write("    private final String unicode;\n\n")
    f.write("    FontAwesome7Free(String unicode) {\n")
    f.write("        this.unicode = unicode;\n")
    f.write("    }\n\n")
    f.write("    public String unicode() {\n")
    f.write("        return unicode;\n")
    f.write("    }\n\n")
    f.write("    @Override\n")
    f.write("    public String toString() {\n")
    f.write("        return unicode;\n")
    f.write("    }\n\n")
    f.write("    // Hilfsmethode für JavaFX Label\n")
    f.write("    public javafx.scene.control.Label createLabel(javafx.scene.text.Font font, double size) {\n")
    f.write("        javafx.scene.control.Label l = new javafx.scene.control.Label(this.unicode);\n")
    f.write("        l.setFont(javafx.scene.text.Font.font(font.getFamily(), size));\n")
    f.write("        return l;\n")
    f.write("    }\n")
    f.write("}\n")

print(f"Java Enum generiert: {output_file}")
