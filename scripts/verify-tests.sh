#!/bin/bash

# Script to verify test structure and identify potential compilation issues

echo "🔍 Verifying test structure..."
echo ""

# Check import patterns in test files
echo "📋 Checking import patterns in test files:"
echo ""

for test_file in backend/src/test/scala/api/*.scala; do
    if [ -f "$test_file" ]; then
        echo "📁 $(basename "$test_file"):"
        echo "   Imports:"
        grep "^import" "$test_file" | head -5
        echo "   Test class:"
        grep "class.*Test.*extends" "$test_file"
        echo ""
    fi
done

echo "🔍 Checking for potential type mismatches:"
echo ""

# Check for GetNextMove usage
echo "GetNextMove calls:"
grep -n "GetNextMove" backend/src/test/scala/api/*.scala 2>/dev/null || echo "No GetNextMove calls found"

echo ""

# Check for Board vs String usage
echo "Board creation patterns:"
grep -n "StandardBoard\|Board\.standardFromMoveStrings" backend/src/test/scala/api/*.scala 2>/dev/null || echo "No board creation patterns found"

echo ""

# Check test method patterns
echo "Test method patterns:"
grep -n "test(" backend/src/test/scala/api/*.scala 2>/dev/null | head -5

echo ""

# Check for potential actor system issues
echo "Actor system usage:"
grep -n "ActorTestKit\|system" backend/src/test/scala/api/*.scala 2>/dev/null || echo "No actor system usage found"

echo ""

echo "✅ Verification complete"
echo ""
echo "💡 If any issues are found above, check:"
echo "   - Import statements match existing project patterns"
echo "   - GetNextMove uses Board objects, not Strings"
echo "   - StandardBoard.StartingPosition for initial board"
echo "   - Board.standardFromMoveStrings() for boards with moves"