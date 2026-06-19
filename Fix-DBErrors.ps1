# Fix-DBErrors.ps1 - Reemplaza catch(SQLException) que tragan errores en todas las D*.java
$scriptPath = "d:\MarioUniv\matoneadaCarla2026\tecno-email\src\main\java\com\tecnoweb\grupo24sa\data"

Get-ChildItem "$scriptPath\*.java" | ForEach-Object {
    $path = $_.FullName
    $text = [System.IO.File]::ReadAllText($path)
    $original = $text
    $name = $_.BaseName

    # Pattern 1: catch(SQLException) que retorna "Error: " + mensaje (métodos save/update/delete)
    # Estos ya muestran el error, los dejamos igual pero mejoramos el mensaje
    $text = $text -replace  'return "Error: " \+ e\.getMessage\(\);\s*\n\s*\}',
                           'return "Error de BD: " + e.getMessage();`n        }'

    # Pattern 2: catch(SQLException) con System.out.println y return null
    $text = $text -replace  'catch \(SQLException e\) \{\s*\n\s*System\.out\.println\("Error: " \+ e\.getMessage\(\)\);\s*\n\s*return null;\s*\n\s*\}',
                           "catch (SQLException e) {`n            System.err.println(""Error en ${name}: "" + e.getMessage());`n            throw new RuntimeException(""Error de conexion a la base de datos: "" + e.getMessage());`n        }"

    # Pattern 3: catch(SQLException) con System.out.println SIN return (los findAll que retornan lista vacia)
    $text = $text -replace  'catch \(SQLException e\) \{\s*\n\s*System\.out\.println\("Error: " \+ e\.getMessage\(\)\);\s*\n\s*\}',
                           "catch (SQLException e) {`n            System.err.println(""Error en ${name}: "" + e.getMessage());`n            throw new RuntimeException(""Error de conexion a la base de datos: "" + e.getMessage());`n        }"

    if ($text -ne $original) {
        [System.IO.File]::WriteAllText($path, $text, [System.Text.UTF8Encoding]::new($false))
        Write-Host "Fixed: $name"
    } else {
        Write-Host "OK: $name (no changes)"
    }
}

Write-Host "DONE"
