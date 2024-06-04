# Gatling Corp Scala interview project

Thank you for taking the time to review this test 🙇

## Getting started

You are most likely more experienced than me in Scala and how this project works so I'm guessing that you don't help.

Anyhow, here's a little recap just in case :

### Running the tests

```console
sbt test
```

### Starting the server

```console
sbt run
```

You can then access the documentation API use this [url](http://localhost:9000/docs/index.html) and run queries

## About the test

I tried to split my work into differents commits so that you can follow my logic. Feel free to scroll through them [here](https://github.com/HugoFouques/scala-interview-project/commits/main/)

I'm not very familiar with the Scala synthax so I took inspiration for various sources (Documentation, blogposts, github, ...). I tried to keep the resulting style as coherent as possible. Any suggestions on that part are very welcome!

I added a error handling middleware so that the server doesn't always return 500s. It wasn't really asked but I was curious and I thought it could be an interesting topic to discuss.

## The requirements 👇

Create a "concat" method which takes 2 `Option[String]`s and concatenate their contents when both exist. A functional
style is expected.

```scala
def concat(opt1: Option[String],
           opt2: Option[String]): Option[String] = ???

concat(Some("foo"), Some("bar")) // Some("foobar")
concat(Some("foo"), None)        // None
concat(None, Some("bar"))        // None
concat(None, None)               // None
```

You can solve this in the file [`src/test/scala/io/gatling/ConcatSpec.scala`](./src/test/scala/io/gatling/ConcatSpec.scala),
and run it in IntelliJ, or in your console:

```console
./sbtx "testOnly io.gatling.ConcatSpec"
```

## Computer Database Webapp

The major library you'll need to use is [cats effect](https://typelevel.org/cats-effect/docs/2.x/getting-started).
In this exercise, we'll use [Smithy4s](https://disneystreaming.github.io/smithy4s/) as well to generate the web server from documentation.
New to effect systems? Take a look at this [talk](https://www.youtube.com/watch?v=qgfCmQ-2tW0).

You can find here the basis of the project. Feel free to modify the architecture to your taste.

The goal of this part is to create a web app that stores and reads data about computers in a file.

A computer is represented by:

- an id
- a name
- an optional introduced date
- an optional discontinued date

Data is stored in the [computers.json](computers.json) file at the root of the project directory.

### Run the project

You can run the `io.gatling.interview.Main` class from IntelliJ, or in your console:

```console
./sbtx run
```

This will start the web server on port 9000.
You can visit [localhost:9000](http://localhost:9000).
A swagger documentation is automatically added (thanks to smithy4s contracts)

### Specifications

- Add an endpoint to display a single computer (with the ID as parameter)
- Add an endpoint to add a computer to the file (with the name and optional dates as parameters)
