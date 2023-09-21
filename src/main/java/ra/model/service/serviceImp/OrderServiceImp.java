package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ra.model.entity.Order;
import ra.model.entity.Users;
import ra.model.repository.OrderRepository;
import ra.model.service.OrderService;
import ra.payload.response.MessageResponse;
import ra.payload.response.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MailService mailService;
    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order findByID(int orderID) {
        return orderRepository.findById(orderID).get();
    }

    @Override
    public List<Order> findByUser(int userId) {
        return orderRepository.findByUsers_UserId(userId);
    }

    @Override
    public List<OrderResponse> adminGetAllOrder() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (!Objects.equals(order.getOrderStatus(), "OK")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }

    @Override
    public List<OrderResponse> adminGetAllAccepted() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (Objects.equals(order.getOrderStatus(), "Accepted")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }

    @Override
    public List<OrderResponse> adminGetAllDelivery() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (Objects.equals(order.getOrderStatus(), "Delivery")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }

    @Override
    public List<OrderResponse> adminGetAllOK() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (Objects.equals(order.getOrderStatus(), "OK")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }

    @Override
    public List<OrderResponse> adminGetAllCancel() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (Objects.equals(order.getOrderStatus(), "Cancel")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }

    @Override
    public List<OrderResponse> adminGetAllUserCancel() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (Objects.equals(order.getOrderStatus(), "UserCancel")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }

    @Override
    public List<OrderResponse> adminGetAllPending() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = getAllOrder();
        for (Order order : list) {
            if (Objects.equals(order.getOrderStatus(), "Pending")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }
}