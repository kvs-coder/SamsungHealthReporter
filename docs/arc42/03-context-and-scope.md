# 3. Context and Scope

## 3.1 Business Context

```mermaid
flowchart LR
    user(["Phone user<br/><small>owns the data, grants permissions</small>"])
    app["Consumer app<br/><small>Android app or Flutter plugin<br/>using SamsungHealthReporter</small>"]
    shealth[["Samsung Health<br/><small>stores health data,<br/>shows permission screens</small>"]]
    devices[["Wearables & accessories<br/><small>Galaxy Watch, Ring, scales, monitors</small>"]]
    samsung[["Samsung partner program<br/><small>approves apps for writing<br/>and distribution</small>"]]

    user -->|uses| app
    user -->|grants permissions, records data| shealth
    app -->|reads, writes, observes data| shealth
    devices -->|sync measurements| shealth
    samsung -->|allow-lists approved apps| shealth
```

| Partner | Input to the library | Output of the library |
| :--- | :--- | :--- |
| Consumer app | Types, time ranges, payloads to write, an `Activity` for permission and resolution screens | Payloads, aggregates, change sets, permissions, devices, typed errors |
| Samsung Health | Data points, aggregates, changes, granted permissions, errors | Read / aggregate / write / change requests, permission requests |
| Phone user | Consent on Samsung Health's permission screen | — |

## 3.2 Technical Context

```mermaid
flowchart LR
    subgraph phone["Android phone"]
        subgraph appproc["Consumer app process"]
            code["App code"] --> lib["SamsungHealthReporter"] --> sdk["Samsung Health Data SDK<br/><small>AAR</small>"]
        end
        shealth[["Samsung Health app<br/><small>com.sec.android.app.shealth<br/>PrivilegedHealthService, data store, permission UI</small>"]]
    end
    cloud[("Samsung account / cloud")]

    sdk -->|"Binder IPC (bound service);<br/>Activity for permission UI"| shealth
    shealth -->|syncs| cloud
```

* The library calls only the SDK; the SDK binds to Samsung Health's service in its own process
  (`SHD#PrivilegedHealthService` in logcat) and verifies the calling package (signature checks are bypassed in
  developer mode).
* `SamsungHealthReporter.isAvailable` queries the package manager for `com.sec.android.app.shealth`; the library
  manifest declares the matching `<queries>` entry.
* Out of scope: Health Connect, Google Fit, Samsung Health Sensor SDK (raw watch sensors), server-side Samsung
  Health APIs, and the old Samsung Health SDK for Android.
