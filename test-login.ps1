# FileServer Login Test Script

Write-Host "=== FileServer Login Test ===" -ForegroundColor Green

# Test 1: Protected page should redirect to login
Write-Host "`n1. Testing unauthenticated access to protected page..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:9101/page/" -MaximumRedirection 0 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 302) {
        Write-Host "OK: Correctly redirected to login page (302)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Not redirected correctly" -ForegroundColor Red
    }
} catch {
    if ($_.Exception.Response.StatusCode -eq 302) {
        Write-Host "OK: Correctly redirected to login page (302)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Test failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 2: Login page should be accessible
Write-Host "`n2. Testing login page access..." -ForegroundColor Yellow
try {
    $loginPage = Invoke-WebRequest -Uri "http://localhost:9101/login"
    if ($loginPage.StatusCode -eq 200 -and $loginPage.Content -like "*FileServer*") {
        Write-Host "OK: Login page displays correctly" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Login page display error" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL: Cannot access login page: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Login functionality
Write-Host "`n3. Testing login functionality..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:9101/login" -Method POST -Body "username=admin&password=admin123" -ContentType "application/x-www-form-urlencoded" -MaximumRedirection 0 -ErrorAction SilentlyContinue
    if ($loginResponse.StatusCode -eq 302) {
        $location = $loginResponse.Headers.Location
        Write-Host "OK: Login successful, redirected to: $location" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Login failed" -ForegroundColor Red
    }
} catch {
    if ($_.Exception.Response.StatusCode -eq 302) {
        Write-Host "OK: Login successful (302 redirect)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Login test failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 4: Wrong login
Write-Host "`n4. Testing wrong login..." -ForegroundColor Yellow
try {
    $wrongLoginResponse = Invoke-WebRequest -Uri "http://localhost:9101/login" -Method POST -Body "username=admin&password=wrongpassword" -ContentType "application/x-www-form-urlencoded" -MaximumRedirection 0 -ErrorAction SilentlyContinue
    if ($wrongLoginResponse.StatusCode -eq 302) {
        $location = $wrongLoginResponse.Headers.Location
        if ($location -like "*error=true*") {
            Write-Host "OK: Wrong login handled correctly, redirected to error page" -ForegroundColor Green
        } else {
            Write-Host "FAIL: Wrong login handling error" -ForegroundColor Red
        }
    }
} catch {
    if ($_.Exception.Response.StatusCode -eq 302) {
        Write-Host "OK: Wrong login handled correctly (302 redirect)" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Wrong login test failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Test 5: Health check endpoint
Write-Host "`n5. Testing health check endpoint..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-WebRequest -Uri "http://localhost:9101/health"
    if ($healthResponse.StatusCode -eq 200) {
        $healthData = $healthResponse.Content | ConvertFrom-Json
        if ($healthData.success -eq $true) {
            Write-Host "OK: Health check normal: $($healthData.result)" -ForegroundColor Green
        } else {
            Write-Host "FAIL: Health check returned error" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "FAIL: Health check failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green
Write-Host "`nTest User Accounts:" -ForegroundColor Cyan
Write-Host "Admin: admin / admin123" -ForegroundColor White
Write-Host "User: user / admin123" -ForegroundColor White
Write-Host "`nApplication URL: http://localhost:9101" -ForegroundColor Cyan
