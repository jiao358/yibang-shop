import { ArrowLeft, MoreHorizontal, Target } from 'lucide-react'
import exampleImage from 'figma:asset/31312417d380f5a618e872fdacf8674101e26075.png'

export function InviteEarningsPage() {
  return (
    <div className="bg-gray-50 min-h-screen">
      {/* Status Bar */}
      <div className="flex justify-between items-center px-4 py-1 text-sm text-gray-600 bg-white">
        <span>17:29</span>
        <div className="flex items-center gap-1">
          <span className="text-xs">100</span>
          <div className="w-4 h-2 border border-gray-400 rounded-sm">
            <div className="w-full h-full bg-green-500 rounded-sm"></div>
          </div>
        </div>
      </div>

      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-white border-b border-gray-100">
        <ArrowLeft className="w-6 h-6 text-gray-600" />
        <h1 className="text-lg text-black">邀请赚佣金</h1>
        <div className="flex items-center gap-3">
          <MoreHorizontal className="w-5 h-5 text-gray-600" />
          <Target className="w-5 h-5 text-gray-600" />
        </div>
      </div>

      {/* Stats Cards */}
      <div className="bg-white mx-4 mt-4 rounded-lg p-4">
        <div className="grid grid-cols-4 gap-4 text-center">
          <div>
            <div className="text-2xl text-black mb-1">0人</div>
            <div className="text-sm text-gray-500">邀请好友 {'>'}</div>
          </div>
          <div>
            <div className="text-2xl text-black mb-1">0人</div>
            <div className="text-sm text-gray-500">认证网拍 {'>'}</div>
          </div>
          <div>
            <div className="text-2xl text-black mb-1">0人</div>
            <div className="text-sm text-gray-500">邀请活跃主 {'>'}</div>
          </div>
          <div>
            <div className="text-2xl text-black mb-1">0.00</div>
            <div className="text-sm text-gray-500">邀请总佣金 {'>'}</div>
          </div>
        </div>
      </div>

      {/* Content Sections */}
      <div className="px-4 mt-6 space-y-6">
        {/* 邀请模特说明 */}
        <div className="bg-white rounded-lg p-4">
          <h3 className="text-lg text-black mb-3">邀请模特说明</h3>
          <p className="text-gray-600 leading-relaxed">
            被邀请模特每完成一单，您都可以获得0.5元/单奖励。
          </p>
        </div>

        {/* 邀请活店主说明 */}
        <div className="bg-white rounded-lg p-4">
          <h3 className="text-lg text-black mb-3">邀请活店主说明</h3>
          <p className="text-gray-600 leading-relaxed">
            被邀请活店主每完成一单，您都可以获得1元/单奖励。
          </p>
        </div>

        {/* 注明 */}
        <div className="bg-white rounded-lg p-4">
          <h3 className="text-lg text-black mb-3">注明</h3>
          <div className="space-y-2 text-gray-600">
            <p>• 所有奖励上不封顶，长期有效！</p>
            <p>• 禁止推广自己的作假行为，发现将取消资格</p>
            <p>• 最终解释权归美眉拍豆腐所有</p>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="fixed bottom-20 left-0 right-0 px-4">
        <div className="grid grid-cols-2 gap-4">
          <button className="bg-pink-400 text-white py-3 rounded-lg">
            邀请模特
          </button>
          <button className="bg-pink-400 text-white py-3 rounded-lg">
            邀请活店主
          </button>
        </div>
      </div>
    </div>
  )
}