#!/bin/bash

# Simple compilation test script for local troubleshooting
# This script tests compilation without Docker dependencies

set -e

echo "🔧 Testing compilation without Docker..."
echo ""

# Check if we have the necessary tools (this would need to be adapted based on environment)
if command -v java >/dev/null 2>&1; then
    echo "✓ Java found: $(java -version 2>&1 | head -n 1)"
else
    echo "❌ Java not found"
    exit 1
fi

echo ""
echo "📁 Current directory: $(pwd)"
echo "📋 Files in project root:"
ls -la

echo ""
echo "🏗️  Would attempt to compile backend..."
echo "Command that would run: sbt backend/compile"

echo ""
echo "🧪 Would attempt to run tests..."  
echo "Command that would run: sbt backend/test"

echo ""
echo "🎯 Would attempt to compile frontend..."
echo "Command that would run: sbt frontend/fastLinkJS"

echo ""
echo "💡 This is a dry-run script for troubleshooting."
echo "💡 To actually run compilation, you need sbt installed."
echo "💡 Install sbt: https://www.scala-sbt.org/download.html"

echo ""
echo "📋 Project structure check:"
echo "Backend source files:"
if [ -d "backend/src" ]; then
    find backend/src -name "*.scala" | head -5
    echo "... (showing first 5 files)"
else
    echo "❌ backend/src directory not found"
fi

echo ""
echo "Frontend source files:"
if [ -d "frontend/src" ]; then
    find frontend/src -name "*.scala" | head -5
    echo "... (showing first 5 files)"
else
    echo "❌ frontend/src directory not found"
fi

echo ""
echo "Build configuration:"
if [ -f "build.sbt" ]; then
    echo "✓ build.sbt found"
    echo "Dependencies summary:"
    grep -E "(scalaVersion|libraryDependencies|%)" build.sbt | head -10
else
    echo "❌ build.sbt not found"
fi

echo ""
echo "🔍 Looking for potential compilation issues..."

echo "Checking for common import patterns:"
echo "Model imports in tests:"
grep -r "import model" backend/src/test/ 2>/dev/null | head -3 || echo "No model imports found"

echo ""
echo "ScalaTest patterns:"
grep -r "extends.*Test\|extends.*Suite" backend/src/test/ 2>/dev/null | head -3 || echo "No test patterns found"

echo ""
echo "✅ Compilation check complete (dry-run)"
echo "To actually compile, run: sbt backend/compile backend/test"