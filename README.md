# VNGTest
## Đề tài: Tìm các vị trí xung quanh mình sử dụng API của google.
1. Các hướng xử lý tối ưu: 
  * Sử dụng đa luồng để lấy dữ liệu từ Google API về. (Cụ thể là dùng AsyncTask)
  * Sử dụng recyclerView với class ViewHolder giúp tăng hiệu suất trong quá trình inflate layout.
2. Kiểm tra ứng dụng: 
  * Kiểm tra trên thiết bị Google Nexus 7, Hệ điều hành Android 6.0.
  * Điều kiện: Thiết bị sử dụng phải bật định vị của google và có kết nối mạng
  * Ứng dụng hiển thị được danh sách các vị trí gần với vị trí hiện tại và tính được khoảng cách tới vị trí hiện tại
  * Khi nhấn vào một địa điểm bất kì sẽ xuất hiện thống tin chi tiết của vị trí đó.
