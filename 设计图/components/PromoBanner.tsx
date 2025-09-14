export function PromoBanner() {
  return (
    <div className="mx-4 mt-2 mb-4 rounded-xl bg-gradient-to-r from-orange-100 to-red-100 p-4 relative overflow-hidden">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg text-gray-800 mb-2">外卖红包天天领</h2>
          <button className="bg-red-500 text-white px-4 py-1 rounded-full text-sm">
            立即领取
          </button>
        </div>
        <div className="relative">
          {/* Gift box illustration */}
          <div className="w-16 h-16 bg-gradient-to-br from-red-400 to-red-600 rounded-lg flex items-center justify-center">
            <div className="w-8 h-8 bg-yellow-300 rounded-full flex items-center justify-center">
              <span className="text-red-600">¥</span>
            </div>
          </div>
          {/* Decorative elements */}
          <div className="absolute -top-2 -right-2 w-4 h-4 bg-yellow-300 rounded transform rotate-45"></div>
          <div className="absolute -bottom-1 -left-1 w-3 h-3 bg-orange-300 rounded"></div>
        </div>
      </div>
      
      {/* Pagination dots */}
      <div className="flex justify-center mt-3 gap-1">
        <div className="w-2 h-2 bg-red-400 rounded-full"></div>
        <div className="w-2 h-2 bg-gray-300 rounded-full"></div>
        <div className="w-2 h-2 bg-gray-300 rounded-full"></div>
        <div className="w-2 h-2 bg-gray-300 rounded-full"></div>
      </div>
    </div>
  )
}