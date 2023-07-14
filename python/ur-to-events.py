#!/usr/bin/env python3
# Transform unified-representation JSON to sink events.
#
import json
import argparse
import sys

OBJECT_OPEN = "object_open"

OBJECT_CLOSE = "object_close"

ARRAY_OPEN = "array_open"

ARRAY_CLOSE = "array_close"

NEXT_KEY = "key"

VALUE = "value"


def _parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument("input", help="Path to a file or '-' for stdin.")
    parser.add_argument("output", help="Path to a file or '-' for stdout.")
    return vars(parser.parse_args())


def main(args):
    content = load_input(args["input"])
    ur = produce_events(content)
    save_result(args["output"], ur)


def load_input(path: str):
    if path == "-":
        return json.load(sys.stdin)
    with open(path, encoding="utf-8") as stream:
        return json.load(stream)


def produce_events(content):
    """Recursive function."""
    result = []
    if isinstance(content, dict):
        result.append([OBJECT_OPEN])
        for key, value in content.items():
            result.append([NEXT_KEY, key])
            result.extend(produce_events(value))
        result.append([OBJECT_CLOSE])
    elif isinstance(content, list):
        result.append([ARRAY_OPEN])
        for value in content:
            result.extend(produce_events(value))
        result.append([ARRAY_CLOSE])
    else:
        result.append([VALUE, content])
    return result


def save_result(path: str, content):
    lines = [" ".join(item) + "\n" for item in content]
    if path == "-":
        sys.stdout.writelines(lines)
    else:
        with open(path, "w", encoding="utf-8") as stream:
            stream.writelines(lines)


if __name__ == "__main__":
    main(_parse_arguments())
