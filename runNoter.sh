args="$@"
echo "run Gradle?[y/n](default is y): "
read -r confirm

if [[ "$confirm" == "" ]]; then

    if [[ -n "$args" ]]; then
        ./gradlew run --args="$args"

    else
        ./gradlew run

    fi

else
    echo "Halting process"

fi

echo ""

