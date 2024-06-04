$version: "2"

namespace io.gatling.interview.api

use alloy#simpleRestJson

@simpleRestJson
service ComputerDatabaseEndpoints {
    version: "1.0.0"
    operations: [
        ListComputers
        GetComputer
        CreateComputer
    ]
}

@readonly
@http(method: "GET", uri: "/computers", code: 200)
operation ListComputers {
    output: ComputersOutput
}

@readonly
@http(method: "GET", uri: "/computers/{id}", code: 200)
operation GetComputer {
    input: GetComputerInput
    output: Computer
}

@http(method: "POST", uri: "/computers", code: 201)
operation CreateComputer {
    input: CreateComputerInput
    output: Computer
}

structure ComputersOutput {
    @required
    computers: Computers
}

list Computers {
    member: Computer
}

structure Computer {
    @required
    id: Long
    @required
    name: String
    introduced: String
    discontinued: String
}

structure GetComputerInput {
    @required
    @httpLabel
    id: Long
}

structure CreateComputerInput {
    @httpPayload
    @required
    content: CreateComputerPayload
}

structure CreateComputerPayload {
    @required
    name: String
    introduced: String
    discontinued: String
}
