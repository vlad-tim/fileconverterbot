#!/bin/bash

find ../.. -name "*.kt" | xargs cat | grep "[a-Z0-9]" | wc -l