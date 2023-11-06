Feedback and comments
=============

### Architecture approach
While working on the assignment I've used the following architectural patterns:
- Ports and adapters architecture  a.k.a. hexagonal architecture
- Vertical slices - independent modules/features for orders and payments
- Event Processing used to implement Shared Kernel
- DDD rich domain model

### Implementation approach
- TDD (junit tests + integration tests)

### Comments
- The description of the task is confusing as it doesn't explain how already existing code should be connected with new requirements
- So I assume that merchant from orders module can be represented by organisation in organisations module (foreign keys TBD)

### Next steps
- Improve DB schema by introducing foreign keys between tables
- Implement payments domain, which is currently not finished
