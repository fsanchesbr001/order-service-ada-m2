param(
    [string[]]$Scopes = @("orders:read", "orders:write", "payments:read", "payments:write"),
    [string]$Secret = "change-me-in-production-32chars!",
    [string]$Issuer = "order-service",
    [string]$Subject = "local-user",
    [int]$ExpiresInSeconds = 3600
)

function ConvertTo-Base64Url {
    param([byte[]]$Bytes)
    $base64 = [Convert]::ToBase64String($Bytes)
    return $base64.TrimEnd("=") -replace "\+", "-" -replace "/", "_"
}

function FormatDuration {
    param([TimeSpan]$TimeSpan)

    if ($TimeSpan.TotalSeconds -lt 60) {
        return "$([Math]::Round($TimeSpan.TotalSeconds)) segundos"
    }
    if ($TimeSpan.TotalMinutes -lt 60) {
        return "$([Math]::Round($TimeSpan.TotalMinutes)) minutos"
    }
    if ($TimeSpan.TotalHours -lt 24) {
        return "$([Math]::Round($TimeSpan.TotalHours, 1)) horas"
    }
    return "$([Math]::Round($TimeSpan.TotalDays, 1)) dias"
}

$secretBytesLength = [Text.Encoding]::UTF8.GetByteCount($Secret)
if ($secretBytesLength -lt 32) {
    Write-Host "Erro: Secret deve ter pelo menos 32 bytes para HS256." -ForegroundColor Red
    Write-Host "Secret atual: $secretBytesLength bytes." -ForegroundColor Red
    exit 1
}

try {
    $now = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
    $expiresAt = $now + $ExpiresInSeconds
    $expiresAtUtc = [DateTimeOffset]::FromUnixTimeSeconds($expiresAt).UtcDateTime.ToString("yyyy-MM-dd HH:mm:ss")

    $headerJson = '{"alg":"HS256","typ":"JWT"}'
    $scopeValue = ($Scopes | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }) -join " "
    $payloadJson = "{`"sub`":`"$Subject`",`"iss`":`"$Issuer`",`"scope`":`"$scopeValue`",`"iat`":$now,`"exp`":$expiresAt}"

    $header = ConvertTo-Base64Url ([Text.Encoding]::UTF8.GetBytes($headerJson))
    $payload = ConvertTo-Base64Url ([Text.Encoding]::UTF8.GetBytes($payloadJson))
    $message = "$header.$payload"

    $hmac = New-Object System.Security.Cryptography.HMACSHA256
    $hmac.Key = [Text.Encoding]::UTF8.GetBytes($Secret)
    $signature = ConvertTo-Base64Url $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($message))

    $token = "$message.$signature"

    Write-Host ""
    Write-Host "JWT token gerado com sucesso." -ForegroundColor Green
    Write-Host "---------------------------------------------------------------" -ForegroundColor Cyan
    Write-Host "Token:" -ForegroundColor Cyan
    Write-Host $token -ForegroundColor Yellow
    Write-Host "---------------------------------------------------------------" -ForegroundColor Cyan
    Write-Host "Issuer:    $Issuer" -ForegroundColor Gray
    Write-Host "Subject:   $Subject" -ForegroundColor Gray
    Write-Host "Scopes:    $scopeValue" -ForegroundColor Gray
    Write-Host "Expira em: $(FormatDuration (New-TimeSpan -Seconds $ExpiresInSeconds)) ($expiresAtUtc UTC)" -ForegroundColor Gray
    Write-Host ""

    if (Get-Command Set-Clipboard -ErrorAction SilentlyContinue) {
        $token | Set-Clipboard
        Write-Host "Token copiado para a area de transferencia." -ForegroundColor Green
    }

    Write-Host ""
    Write-Host "Swagger: http://localhost:8080/swagger-ui/index.html" -ForegroundColor White
    Write-Host "No Authorize, cole o token sem o prefixo 'Bearer '." -ForegroundColor White
    Write-Host ""
    Write-Host "Curl exemplo:" -ForegroundColor White
    Write-Host "curl -X GET 'http://localhost:8080/api/v1/orders?customerId=1' -H 'Authorization: Bearer <TOKEN>' -H 'Content-Type: application/json'" -ForegroundColor White
}
catch {
    Write-Host "Erro ao gerar JWT: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
