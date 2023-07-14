# Specification
You can generate [Bikeshed](https://speced.github.io/bikeshed/) specification using following command.
```shell
docker run --rm -v "${PWD}:/data" bikeshed:latest bikeshed watch /data/specification.bs /data/specification.html
```
This would automatically update the produced file when source is changed.
