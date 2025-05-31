$pieces = @("wP", "wN", "wB", "wR", "wQ", "wK", "bP", "bN", "bB", "bR", "bQ", "bK")
$baseUrl = "https://unpkg.com/@chrisoakman/chessboardjs@1.0.0/dist/img/chesspieces/wikipedia"
$outputDir = "frontend/src/main/resources/img/chesspieces/wikipedia"

foreach ($piece in $pieces) {
    $url = "$baseUrl/$piece.png"
    $outputFile = "$outputDir/$piece.png"
    Write-Host "Downloading $piece.png..."
    Invoke-WebRequest -Uri $url -OutFile $outputFile
}

Write-Host "All pieces downloaded successfully!" 