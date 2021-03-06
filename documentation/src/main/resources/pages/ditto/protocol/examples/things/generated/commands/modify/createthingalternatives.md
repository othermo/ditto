## Alternative CreateThing commands

If you want to copy an existing Policy instead of creating a new one by yourself or reference an existing Policy, you
can adjust the CreateThing command like demonstrated in the following examples.

### CreateThing with copied Policy by Policy ID

Creates a new Thing with ID ``com.acme:xdk_53`` with a Policy copied from the Policy with ID ``com.acme:the_policy_id_to_copy``.

```json
{
  "topic": "com.acme/xdk_53/things/twin/commands/create",
  "headers": {},
  "path": "/",
  "value": {
    "thingId": "com.acme:xdk_53",
    "policyId": "com.acme:the_policy_id",
    "_copyPolicyFrom": "com:acme:the_policy_id_to_copy"
  }
}
```

### CreateThing with copied Policy by Thing reference

Creates a new Thing with ID ``com.acme:xdk_53`` with a Policy copied from a Thing with ID ``com.acme:xdk_52``.

```json
{
  "topic": "com.acme/xdk_53/things/twin/commands/create",
  "headers": {},
  "path": "/",
  "value": {
    "thingId": "com.acme:xdk_53",
    "policyId": "com.acme:the_policy_id",
    "_copyPolicyFrom": "{% raw %}{{ ref:things/com:acme:xdk_52/policyId }}{% endraw %}"
  }
}
```
