import { ArrowLeft, MoreHorizontal, Target } from 'lucide-react'

export function MessagePage() {
  return (
    <div className="bg-white min-h-screen">
      {/* Status Bar */}
      <div className="flex justify-between items-center px-4 py-1 text-sm text-gray-600">
        <span>17:30</span>
        <div className="flex items-center gap-1">
          <span className="text-xs">100</span>
          <div className="w-4 h-2 border border-gray-400 rounded-sm">
            <div className="w-full h-full bg-green-500 rounded-sm"></div>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
        <ArrowLeft className="w-6 h-6 text-gray-600" />
        <h1 className="text-lg text-black">消息</h1>
        <div className="flex items-center gap-3">
          <MoreHorizontal className="w-5 h-5 text-gray-600" />
          <Target className="w-5 h-5 text-gray-600" />
        </div>
      </div>

      {/* Empty State */}
      <div className="flex flex-col items-center justify-center flex-1 px-8 py-16">
        <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <span className="text-3xl">💬</span>
        </div>
        <p className="text-gray-400 text-center">暂无消息</p>
      </div>
    </div>
  )
}