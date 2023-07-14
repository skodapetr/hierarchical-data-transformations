# Specification
You can generate [Bikeshed](https://speced.github.io/bikeshed/) specification using following command.
```shell
docker run --rm -v "${PWD}:/data" bikeshed:latest bikeshed watch /data/transformation.bs /data/transformation.html
```
This would automatically update the produced file when source is changed.
