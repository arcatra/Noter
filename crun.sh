read -r -p "Do you want to run the project?[y/n] : " confirm

if [[ $confirm == "y" ]]; then
    ./gradlew run

else
    echo "Halting the process"

fi

echo ""

read -r -p "Do you want to build the project?[y/n] : " bConfirm
if [[ $bConfirm == "y" ]]; then
    echo "Compiling the source code..."
    ./gradlew build

    echo "Done"

else
    echo "Skipping Compilation.."
    echo ""

fi
