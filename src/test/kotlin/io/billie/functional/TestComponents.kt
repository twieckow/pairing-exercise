package io.billie.functional

import io.billie.functional.tools.AllEventsCollectingListener

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import


@TestConfiguration
@Import(AllEventsCollectingListener::class)
class TestComponents