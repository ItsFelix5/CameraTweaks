name: Suggestion
description: Request a new feature or a change to an existing one.
title: '[Suggestion] '
labels:
  - Incompatibility
assignees:
  - itsfelix5
body:
  - type: checkboxes
    attributes:
      label: Please confirm the following.
      options:
        - label: I have checked there is no [existing issue](https://github.com/ItsFelix5/CameraTweaks/issues?q=is%3Aissue+is%3Aopen+label%3A%22Incompatibility%22) for this
          required: true
        - label: I have checked the mod is up to date
          required: true
        - label: I have checked this is not a mod that has similar features
          required: true
  - type: dropdown
    attributes:
      label: What kind of issue is this
      options:
        - Crash
        - Visual Issue
        - Other
    validations:
      required: true
  - type: input
    attributes:
      label: Modrinth link
    validations:
      required: true
  - type: input
    attributes:
      label: Minecraft version
    validations:
      required: true
  - type: textarea
    attributes:
      label: Describe the bug
      placeholder: >-
        A clear and concise description of what the bug is, screenshots if a visual issue etc.
    validations:
      required: true
