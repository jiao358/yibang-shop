# HSF 服务 API Key 认证使用文档

## 概述

HSF（High-speed Service Framework）服务采用 API Key 认证机制，专门为内部系统提供时间相关的工具方法。外部系统通过 API Key 可以绕过 JWT 认证直接调用 HSF 服务。

## 认证机制

### API Key 格式
- 格式：`系统名-年份-随机字符串`
- 示例：`erp-2024-abc123def456`

### 请求头
所有请求必须包含以下请求头：
```
X-API-Key: your-api-key-here
```

## 已配置的内部系统

| 系统名称 | API Key | 说明 |
|---------|---------|------|
| yibang-erp | erp-2024-abc123def456 | 懿邦ERP系统 |
| other-system | other-2024-xyz789 | 其他内部系统 |

## API 接口列表

### 基础信息
- **基础路径**: `/api/hsf/time`
- **认证方式**: API Key
- **响应格式**: JSON

### 1. 获取当前时间
```http
GET /api/hsf/time/current
X-API-Key: erp-2024-abc123def456
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "2024-01-15T10:30:45",
  "timestamp": 1705287045000
}
```

### 2. 获取当前时间戳（毫秒）
```http
GET /api/hsf/time/timestamp
X-API-Key: erp-2024-abc123def456
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1705287045000,
  "timestamp": 1705287045000
}
```

### 3. 获取当前时间戳（秒）
```http
GET /api/hsf/time/timestamp/seconds
X-API-Key: erp-2024-abc123def456
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1705287045,
  "timestamp": 1705287045000
}
```

### 4. 获取格式化的当前时间
```http
GET /api/hsf/time/current/formatted?pattern=yyyy-MM-dd HH:mm:ss
X-API-Key: erp-2024-abc123def456
```

**参数**:
- `pattern` (可选): 时间格式，默认为 `yyyy-MM-dd HH:mm:ss`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "2024-01-15 10:30:45",
  "timestamp": 1705287045000
}
```

### 5. 获取时间信息详情
```http
GET /api/hsf/time/info
X-API-Key: erp-2024-abc123def456
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "currentTime": "2024-01-15T10:30:45",
    "timestamp": 1705287045000,
    "timestampSeconds": 1705287045,
    "formattedTime": "2024-01-15 10:30:45",
    "date": "2024-01-15",
    "timeOnly": "10:30:45",
    "dayOfWeek": 1,
    "year": 2024,
    "month": 1,
    "day": 15
  },
  "timestamp": 1705287045000
}
```

### 6. 获取当前日期
```http
GET /api/hsf/time/date
X-API-Key: erp-2024-abc123def456
```

### 7. 获取当前时间（仅时间部分）
```http
GET /api/hsf/time/time-only
X-API-Key: erp-2024-abc123def456
```

### 8. 获取当前周几
```http
GET /api/hsf/time/day-of-week
X-API-Key: erp-2024-abc123def456
```

### 9. 获取当前年份
```http
GET /api/hsf/time/year
X-API-Key: erp-2024-abc123def456
```

### 10. 获取当前月份
```http
GET /api/hsf/time/month
X-API-Key: erp-2024-abc123def456
```

### 11. 获取当前日期（几号）
```http
GET /api/hsf/time/day
X-API-Key: erp-2024-abc123def456
```

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 401 | 缺少 API Key | 在请求头中添加 `X-API-Key` |
| 401 | 无效的 API Key | 检查 API Key 是否正确，联系管理员 |
| 403 | 禁止访问 | 检查系统是否在允许列表中 |
| 500 | 服务器内部错误 | 联系技术支持 |

## 调用示例

### Java (Spring Boot)
```java
@Service
public class HsfTimeService {
    
    @Value("${hsf.api.key}")
    private String apiKey;
    
    @Value("${hsf.api.url}")
    private String hsfApiUrl;
    
    public String getCurrentTime() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", apiKey);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Result> response = restTemplate.exchange(
            hsfApiUrl + "/api/hsf/time/current",
            HttpMethod.GET,
            entity,
            Result.class
        );
        
        return response.getBody().getData();
    }
}
```

### Python
```python
import requests

def get_current_time():
    headers = {
        'X-API-Key': 'erp-2024-abc123def456'
    }
    
    response = requests.get(
        'http://localhost:8080/api/hsf/time/current',
        headers=headers
    )
    
    if response.status_code == 200:
        data = response.json()
        return data['data']
    else:
        raise Exception(f"API调用失败: {response.status_code}")
```

### cURL
```bash
curl -X GET \
  'http://localhost:8080/api/hsf/time/current' \
  -H 'X-API-Key: erp-2024-abc123def456'
```

## 安全注意事项

1. **API Key 保密**: API Key 相当于密码，请妥善保管，不要泄露
2. **HTTPS 传输**: 生产环境请使用 HTTPS 传输 API Key
3. **定期轮换**: 建议定期更换 API Key
4. **访问日志**: 系统会记录所有 API Key 的访问日志
5. **IP 限制**: 可配置特定 IP 才能使用 API Key

## 新增内部系统

如需新增内部系统，请联系管理员在配置文件中添加：

```yaml
internal:
  api-keys: 
    yibang-erp: erp-2024-abc123def456
    other-system: other-2024-xyz789
    new-system: new-2024-xyz123abc456  # 新增系统
```

## 技术支持

如有问题，请联系：
- 技术负责人：yibang
- 邮箱：tech@yibangkj.com
- 文档更新时间：2024-01-15
