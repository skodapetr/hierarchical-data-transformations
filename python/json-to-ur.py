#!/usr/bin/env python3
# Transform JSON file to unified-representation JSON.
#
import json
import argparse
import sys

KEY_TYPE = "@type"

KEY_VALUE = "@value"

def _parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument("input", help="Path to a file or '-' for stdin.")
    parser.add_argument("output", help="Path to a file or '-' for stdout.")
    return vars(parser.parse_args())


def main(args):
    content = load_input(args["input"])
    ur = produce_unified_representation(content)
    save_result(args["output"], ur)


def load_input(path: str):
    if path == "-":
        return json.load(sys.stdin)
    with open(path, encoding="utf-8") as stream:
        return json.load(stream)


def produce_unified_representation(content):
    """Recursive function."""
    if isinstance(content, dict):
        properties = {}
        for key, value in content.items():
            properties[key] = [produce_unified_representation(value)]
        return {KEY_TYPE: ["object"], **properties}
    elif isinstance(content, list):
        items = {}
        for key, value in enumerate(content):
            items[key] = [produce_unified_representation(value)]
        return {
            KEY_TYPE: ["array"],
            **items,
        }
    else:
        return {KEY_TYPE: [primitive_type(content)], KEY_VALUE: [str(content)]}


def primitive_type(value):
    if isinstance(value, str):
        return "string"
    elif isinstance(value, bool):
        return "boolean"
    elif isinstance(value, int):
        return "number"
    else:
        assert False, "Unsupported type"


def save_result(path: str, content):
    if path == "-":
        json.dump(content, sys.stdout, ensure_ascii=False, indent=2)
    else:
        with open(path, "w", encoding="utf-8") as stream:
            return json.dump(content, stream, ensure_ascii=False, indent=2)


if __name__ == "__main__":
    main(_parse_arguments())
