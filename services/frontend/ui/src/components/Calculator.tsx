import { useState } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card'
import { Input } from './ui/input'
import { Button } from './ui/button'
import { Plus, Minus, X, Divide, RotateCcw, Loader2 } from 'lucide-react'

export function Calculator() {
  const [num1, setNum1] = useState<string>('')
  const [num2, setNum2] = useState<string>('')
  const [result, setResult] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const calculate = async (operation: string) => {
    setError(null)
    setResult(null)
    setLoading(true)

    const n1 = parseFloat(num1)
    const n2 = parseFloat(num2)

    if (isNaN(n1) || isNaN(n2)) {
      setError('Please enter valid numbers')
      setLoading(false)
      return
    }

    try {
      const response = await fetch('/api/v1/calculate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          operation,
          num1: n1,
          num2: n2,
        }),
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || errorData.detail || 'Calculation failed')
      }

      const resultValue = await response.json()
      setResult(resultValue)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred')
    } finally {
      setLoading(false)
    }
  }

  const clear = () => {
    setNum1('')
    setNum2('')
    setResult(null)
    setError(null)
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 dark:from-gray-950 dark:via-gray-900 dark:to-gray-950 p-4">
      <Card className="w-full max-w-md shadow-2xl dark:shadow-primary/20">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
            Calculator
          </CardTitle>
          <CardDescription>
            Distributed microservice calculator
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-4">
            <Input
              type="number"
              placeholder="First number"
              value={num1}
              onChange={(e) => setNum1(e.target.value)}
              className="text-lg"
            />
            <Input
              type="number"
              placeholder="Second number"
              value={num2}
              onChange={(e) => setNum2(e.target.value)}
              className="text-lg"
            />
          </div>

          <div className="grid grid-cols-4 gap-3">
            <Button
              onClick={() => calculate('add')}
              disabled={loading}
              className="h-14 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700"
              size="lg"
            >
              {loading ? <Loader2 className="h-5 w-5 animate-spin" /> : <Plus className="h-5 w-5" />}
            </Button>
            <Button
              onClick={() => calculate('subtract')}
              disabled={loading}
              className="h-14 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700"
              size="lg"
            >
              {loading ? <Loader2 className="h-5 w-5 animate-spin" /> : <Minus className="h-5 w-5" />}
            </Button>
            <Button
              onClick={() => calculate('multiply')}
              disabled={loading}
              className="h-14 bg-gradient-to-r from-yellow-500 to-orange-600 hover:from-yellow-600 hover:to-orange-700"
              size="lg"
            >
              {loading ? <Loader2 className="h-5 w-5 animate-spin" /> : <X className="h-5 w-5" />}
            </Button>
            <Button
              onClick={() => calculate('divide')}
              disabled={loading}
              className="h-14 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700"
              size="lg"
            >
              {loading ? <Loader2 className="h-5 w-5 animate-spin" /> : <Divide className="h-5 w-5" />}
            </Button>
          </div>

          <Button
            onClick={clear}
            variant="outline"
            className="w-full h-12"
            size="lg"
          >
            <RotateCcw className="h-4 w-4 mr-2" />
            Clear
          </Button>

          {(result !== null || error) && (
            <div className="mt-6 p-6 rounded-lg bg-gradient-to-r from-gray-50 to-gray-100 dark:from-gray-800 dark:to-gray-900 border-2 border-gray-200 dark:border-gray-700">
              {error ? (
                <div className="text-center">
                  <p className="text-sm font-semibold text-red-600 dark:text-red-400 mb-1">
                    Error
                  </p>
                  <p className="text-lg text-red-700 dark:text-red-300">{error}</p>
                </div>
              ) : (
                <div className="text-center">
                  <p className="text-sm font-semibold text-gray-600 dark:text-gray-400 mb-1">
                    Result
                  </p>
                  <p className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                    {result}
                  </p>
                </div>
              )}
            </div>
          )}

          {loading && (
            <div className="flex items-center justify-center gap-2 text-sm text-muted-foreground animate-pulse">
              <Loader2 className="h-4 w-4 animate-spin" />
              <span>Calculating...</span>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
