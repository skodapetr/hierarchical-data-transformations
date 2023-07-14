#!/usr/bin/env python3
# Transform sink events to JSON.
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

KEY_TYPE = "@type"

KEY_VALUE = "@value"

def _parse_arguments():
    parser = argparse.ArgumentParser()
    parser.add_argument("input", help="Path to a file or '-' for stdin.")
    parser.add_argument("output", help="Path to a file or '-' for stdout.")
    return vars(parser.parse_args())


def main(args):
    events = load_input(args["input"])
    result = produce_json(events)
    save_result(args["output"], result)
    


def load_input(path: str):
    if path == "-":
        lines = sys.stdin.readlines()
    else:
        with open(path, encoding="utf-8") as stream:
          lines = stream.readlines()
    return [
        line.rstrip().split(" ", 2)
        for line in lines
    ]


def produce_json(events):
    iterator = iter(events)
    event = next(iterator)
    action = event[0]
    if action == OBJECT_OPEN:
        return handle_object(iterator)
    elif action == ARRAY_OPEN:
        return handle_array(iterator)
    else:
        assert False, f"Unexpected first type '{event}'."

def handle_object(iterator):
    collector = {}
    next_key = None
    for event in iterator:
        action = event[0]
        if action == OBJECT_OPEN:
            collector[next_key] = handle_object(iterator)
        elif action == ARRAY_OPEN:
            collector[next_key] = handle_array(iterator)
        elif action == OBJECT_CLOSE:
            # When closing object we may need to evaluate control key.
            result = None
            type = collector[KEY_TYPE]
            if type == "object":
                # Remove @type and return rest of the properties.
                del collector[KEY_TYPE]
                result = collector
            elif type == "array":
                # Remover @type and store other values in an array.
                result = [
                    item
                    for key, item in collector.items()
                    if key not in [KEY_TYPE]
                ]
            elif type == "string":
                result = collector[KEY_VALUE]
            elif type == "number":
                result =  int(collector[KEY_VALUE])
            elif type == "boolean":
                result = bool(collector[KEY_VALUE])
            else:
                assert False, f"Unexpected type '{type}'."
            return result
        elif action == NEXT_KEY:
            next_key = event[1]
        elif action == VALUE:
            collector[next_key] = action[1]
        else:
            assert False, f"Unexpected event '{event}'."



def handle_array(iterator):
    """For JSON there is only one value in the array."""
    result = None
    for event in iterator:
        action = event[0]
        if action == OBJECT_OPEN:
            result = handle_object(iterator)
        elif action == ARRAY_OPEN:
            result = handle_array(iterator)
        elif action == ARRAY_CLOSE:
            return result
        elif action == VALUE:
            result = event[1]
        else:
            assert False, f"Unexpected event '{event}'."

def save_result(path: str, content):
    if path == "-":
        json.dump(content, sys.stdout, ensure_ascii=False, indent=2)
    else:
        with open(path, "w", encoding="utf-8") as stream:
            return json.dump(content, stream, ensure_ascii=False, indent=2)

if __name__ == "__main__":
    main(_parse_arguments())
