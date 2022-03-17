# Test project

Для запуска проекта нужно установить JDK 11 и Maven, 

После установки перейти в папку с проектом и ввести команды
```
mvn test
```
```
mvn compile exec:java -D"exec.args"="data-1 json-1"
```
`data-1` название .bin файла,

`json-1` название .json файла

В папке `project/src/main/java/project/jsonFiles` появится итоговый json файл

Не совсем понял что нужно было сделать с полем "quantity" но надеюсь примерно попал)


