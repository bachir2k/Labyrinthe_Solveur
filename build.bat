@echo off
REM Script de compilation du projet Labyrinthe_Solveur

echo.
echo ===================================
echo Compilation du projet Maven
echo ===================================
echo.

echo [1/3] Nettoyage et compilation...
mvn clean compile
if errorlevel 1 (
    echo Erreur lors de la compilation!
    exit /b 1
)

echo.
echo [2/3] Execution des tests...
mvn test
if errorlevel 1 (
    echo Erreur lors de l'execution des tests!
    exit /b 1
)

echo.
echo [3/3] Creation du JAR...
mvn package
if errorlevel 1 (
    echo Erreur lors de la creation du JAR!
    exit /b 1
)

echo.
echo ===================================
echo Compilation terminée avec succes!
echo ===================================
echo.

echo Voulez-vous lancer l'application?
echo [1] GUI ^(interface graphique^)
echo [2] Console ^(mode texte^)
echo [3] Non, quitter
echo.
set /p choice="Choix: "

if "%choice%"=="1" (
    echo.
    echo Lancement de l'application GUI...
    java -jar target/labyrinthe.jar
) else if "%choice%"=="2" (
    echo.
    echo Lancement de l'application en mode console...
    java -jar target/labyrinthe.jar console
) else (
    echo.
    echo Fin du script.
)

pause
