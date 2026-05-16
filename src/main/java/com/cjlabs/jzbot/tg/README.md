
Asia/Shanghai
Asia/Tokyo
America/New_York
Europe/London
UTC



flowchart TD
Start([Telegram Update]) --> A{Update Type?}

    A -->|Message| B{Is Command?}
    A -->|CallbackQuery| C[CallbackRouter]
    A -->|ChatMember| D[EventRouter]
    A -->|JoinRequest| D
    
    B -->|Yes| E[CommandRouter]
    B -->|No| F[MessageRouter]
    
    E --> G{Find Command Handler}
    G -->|Found| H[Execute Command]
    G -->|Not Found| I[Send Help Message]
    
    H --> J{Pre-Check}
    J -->|Group Only?| K{Is Group?}
    J -->|Private Only?| L{Is Private?}
    J -->|Require Admin?| M{Is Admin?}
    J -->|Pass| N[doExecute]
    
    K -->|No| O[Send Error]
    K -->|Yes| N
    L -->|No| O
    L -->|Yes| N
    M -->|No| O
    M -->|Yes| N
    
    N --> P[Service Layer]
    P --> Q{Success?}
    Q -->|Yes| R[Send Success Response]
    Q -->|No| S[Send Error Response]
    
    F --> T{Check Filters}
    T -->|Keyword Filter| U{Contains Forbidden?}
    T -->|Spam Filter| V{Is Spam?}
    T -->|Pass| W[Handle Regular Message]
    
    U -->|Yes| X[Execute Filter Action]
    U -->|No| W
    V -->|Yes| X
    V -->|No| W
    
    X --> Y{Filter Action}
    Y -->|DELETE| Z[Delete Message]
    Y -->|WARN| AA[Send Warning]
    Y -->|MUTE| AB[Mute User]
    Y -->|KICK| AC[Kick User]
    
    C --> AD[Find Callback Handler]
    AD --> AE[Handle Callback]
    AE --> P
    
    D --> AF{Event Type}
    AF -->|Bot Added| AG[Send Welcome]
    AF -->|User Joined| AH[Check Verification]
    AF -->|User Left| AI[Log Event]
    
    R --> End([Response Sent])
    S --> End
    O --> End
    Z --> End
    AA --> End
    AB --> End
    AC --> End
    AG --> End
    AH --> End
    AI --> End
    W --> End
    
    style Start fill:#e1f5ff
    style End fill:#c8e6c9
    style O fill:#ffcdd2
    style S fill:#ffcdd2