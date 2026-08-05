package com.example.calkgp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.calkgp.R
import com.example.calkgp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardCourt.title.text = "Суды общей юрисдикции"
        binding.cardCourt.subtitle.text = "Ст. 333.19 НК РФ"
        binding.cardCourt.icon.setImageResource(android.R.drawable.ic_menu_sort_by_size)
        binding.cardCourt.card.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_court)
        }

        binding.cardArbitration.title.text = "Арбитражные суды"
        binding.cardArbitration.subtitle.text = "Ст. 333.21 НК РФ"
        binding.cardArbitration.icon.setImageResource(android.R.drawable.ic_menu_agenda)
        binding.cardArbitration.card.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_arbitration)
        }

        binding.cardNotary.title.text = "Нотариальные действия"
        binding.cardNotary.subtitle.text = "Ст. 333.24-333.26 НК РФ"
        binding.cardNotary.icon.setImageResource(android.R.drawable.ic_menu_edit)
        binding.cardNotary.card.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_notary)
        }

        binding.cardRegistration.title.text = "Регистрация"
        binding.cardRegistration.subtitle.text = "ЗАГС, Росреестр, ФНС — ст. 333.33 НК РФ"
        binding.cardRegistration.icon.setImageResource(android.R.drawable.ic_menu_add)
        binding.cardRegistration.card.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_registration)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
